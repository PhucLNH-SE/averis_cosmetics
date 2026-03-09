(function () {
    "use strict";

    var form = null;
    var provinceSelect = null;
    var wardField = null;
    var districtField = null;
    var streetInput = null;
    var suggestionBox = null;
    var selectedAddressIdInput = null;
    var selectedAddressLatInput = null;
    var selectedAddressLonInput = null;
    var originalStreetInput = null;
    var addressApiStatusEl = null;
    var geoAddressStatusEl = null;

    var apiUrl = "/address-api";
    var geoapifyApiKey = "";
    var requireAddressSuggestion = false;

    var map = null;
    var marker = null;

    var debounceTimer = null;
    var latestSuggestionRequestId = 0;

    function normalize(value) {
        return (value || "").trim().toLowerCase();
    }

    function setStatus(el, message, type) {
        if (!el) {
            return;
        }

        el.textContent = message || "";
        el.classList.remove("text-muted", "text-danger", "text-success");

        if (type === "error") {
            el.classList.add("text-danger");
        } else if (type === "success") {
            el.classList.add("text-success");
        } else {
            el.classList.add("text-muted");
        }
    }

    function createOption(name, code, isSelected) {
        var option = document.createElement("option");
        option.value = name;
        option.textContent = name;
        option.setAttribute("data-code", String(code || ""));

        if (isSelected) {
            option.selected = true;
        }

        return option;
    }

    function resetSelect(select, placeholder, disableSelect) {
        select.innerHTML = "";

        var placeholderOption = document.createElement("option");
        placeholderOption.value = "";
        placeholderOption.textContent = placeholder;
        placeholderOption.selected = true;
        select.appendChild(placeholderOption);

        select.disabled = !!disableSelect;
    }

    function populateSelect(select, items, selectedName, placeholder) {
        var normalizedSelected = normalize(selectedName);
        var matched = false;

        resetSelect(select, placeholder, false);

        for (var i = 0; i < items.length; i++) {
            var item = items[i];
            var itemName = item.name || "";
            var itemCode = item.code || "";

            var option = createOption(itemName, itemCode, normalize(itemName) === normalizedSelected);
            if (option.selected) {
                matched = true;
            }
            select.appendChild(option);
        }

        if (selectedName && !matched) {
            select.appendChild(createOption(selectedName, "", true));
        }
    }

    function getSelectedCode(select) {
        var selectedOption = select.options[select.selectedIndex];
        if (!selectedOption) {
            return "";
        }
        return selectedOption.getAttribute("data-code") || "";
    }

    function fetchJson(url) {
        return fetch(url, {
            method: "GET",
            headers: {
                "Accept": "application/json"
            }
        }).then(function (response) {
            if (!response.ok) {
                throw new Error("HTTP " + response.status);
            }
            return response.json();
        });
    }

    function fetchAddressData(action, params) {
        var url = new URL(apiUrl, window.location.origin);
        url.searchParams.set("action", action);

        if (params) {
            var keys = Object.keys(params);
            for (var i = 0; i < keys.length; i++) {
                url.searchParams.set(keys[i], params[keys[i]]);
            }
        }

        return fetchJson(url.toString());
    }

    function syncDistrictWithWard() {
        if (districtField && wardField && wardField.value) {
            districtField.value = wardField.value;
        }
    }

    function loadProvinces(selectedProvince) {
        setStatus(addressApiStatusEl, "Loading latest Vietnam provinces...");
        resetSelect(provinceSelect, "Loading provinces...", true);
        resetSelect(wardField, "Select ward", true);
        districtField.value = "";

        return fetchAddressData("provinces").then(function (data) {
            populateSelect(provinceSelect, data || [], selectedProvince, "Select province/city");
        });
    }

    function loadWards(provinceCode, selectedWard) {
        resetSelect(wardField, "Loading wards...", true);
        districtField.value = "";

        if (!provinceCode) {
            resetSelect(wardField, "Select ward", true);
            return Promise.resolve();
        }

        return fetchAddressData("wards", { provinceCode: provinceCode }).then(function (data) {
            var wards = Array.isArray(data) ? data : ((data && Array.isArray(data.wards)) ? data.wards : []);
            populateSelect(wardField, wards, selectedWard, "Select ward");
        });
    }

    function hideSuggestions() {
        if (!suggestionBox) {
            return;
        }
        suggestionBox.innerHTML = "";
        suggestionBox.hidden = true;
    }

    function clearSelectedAddress() {
        if (selectedAddressIdInput) {
            selectedAddressIdInput.value = "";
        }
        if (selectedAddressLatInput) {
            selectedAddressLatInput.value = "";
        }
        if (selectedAddressLonInput) {
            selectedAddressLonInput.value = "";
        }
    }

    function initLeafletMap() {
        var mapContainer = document.getElementById("addressMap");
        if (!mapContainer) {
            return;
        }

        mapContainer.style.minHeight = "320px";
        mapContainer.style.width = "100%";

        if (typeof window.L === "undefined") {
            mapContainer.classList.add("address-map-fallback");
            mapContainer.innerHTML = '<div class="address-map-placeholder">Map cannot be loaded right now.</div>';
            setStatus(geoAddressStatusEl, "Leaflet map library is not available. Check CDN/network.", "error");
            return;
        }

        map = L.map(mapContainer).setView([21.0285, 105.8542], 13);

        L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
            maxZoom: 19,
            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        }).addTo(map);

        marker = L.marker([21.0285, 105.8542], { opacity: 0 }).addTo(map);

        window.setTimeout(function () {
            map.invalidateSize();
        }, 0);
    }

    function setMapLocation(lat, lon, zoom) {
        if (!map || !marker || !isFinite(lat) || !isFinite(lon)) {
            return;
        }

        marker.setLatLng([lat, lon]);
        marker.setOpacity(1);
        map.setView([lat, lon], zoom || 17);
    }

    function chooseSuggestion(result) {
        var formatted = result.formatted || result.address_line1 || result.address_line2 || "";
        var lat = Number(result.lat);
        var lon = Number(result.lon);

        streetInput.value = formatted || streetInput.value;

        if (selectedAddressIdInput) {
            selectedAddressIdInput.value = result.place_id || formatted;
        }
        if (selectedAddressLatInput) {
            selectedAddressLatInput.value = isFinite(lat) ? String(lat) : "";
        }
        if (selectedAddressLonInput) {
            selectedAddressLonInput.value = isFinite(lon) ? String(lon) : "";
        }

        if (isFinite(lat) && isFinite(lon)) {
            setMapLocation(lat, lon, 17);
        }

        hideSuggestions();
        setStatus(geoAddressStatusEl, "Address selected.", "success");
    }

    function renderSuggestions(results) {
        if (!suggestionBox) {
            return;
        }

        suggestionBox.innerHTML = "";

        if (!results || results.length === 0) {
            suggestionBox.hidden = true;
            setStatus(geoAddressStatusEl, "No matching address found.", "error");
            return;
        }

        for (var i = 0; i < results.length; i++) {
            (function (result) {
                var button = document.createElement("button");
                button.type = "button";
                button.className = "list-group-item list-group-item-action";
                button.textContent = result.formatted || result.address_line1 || result.address_line2 || "Unknown address";

                button.addEventListener("click", function () {
                    chooseSuggestion(result);
                });

                suggestionBox.appendChild(button);
            })(results[i]);
        }

        suggestionBox.hidden = false;
    }

    function buildSearchText(userText) {
        var parts = [userText];

        if (wardField && wardField.value) {
            parts.push(wardField.value);
        }
        if (provinceSelect && provinceSelect.value) {
            parts.push(provinceSelect.value);
        }

        parts.push("Viet Nam");
        return parts.join(", ");
    }

    function searchSuggestions(keyword) {
        if (!geoapifyApiKey) {
            setStatus(geoAddressStatusEl, "Geoapify API key is missing.", "error");
            return;
        }

        var requestId = ++latestSuggestionRequestId;
        var url = new URL("https://api.geoapify.com/v1/geocode/autocomplete");
        url.searchParams.set("text", buildSearchText(keyword));
        url.searchParams.set("filter", "countrycode:vn");
        url.searchParams.set("lang", "vi");
        url.searchParams.set("limit", "8");
        url.searchParams.set("format", "json");
        url.searchParams.set("apiKey", geoapifyApiKey);

        fetchJson(url.toString())
            .then(function (data) {
                if (requestId !== latestSuggestionRequestId) {
                    return;
                }

                var results = data && Array.isArray(data.results) ? data.results : [];
                renderSuggestions(results);
            })
            .catch(function () {
                setStatus(geoAddressStatusEl, "Cannot load address suggestions right now.", "error");
            });
    }

    function locateAddressOnMap(addressText) {
        if (!geoapifyApiKey || !addressText) {
            return;
        }

        var url = new URL("https://api.geoapify.com/v1/geocode/search");
        url.searchParams.set("text", addressText + ", Viet Nam");
        url.searchParams.set("filter", "countrycode:vn");
        url.searchParams.set("limit", "1");
        url.searchParams.set("format", "json");
        url.searchParams.set("apiKey", geoapifyApiKey);

        fetchJson(url.toString())
            .then(function (data) {
                var results = data && Array.isArray(data.results) ? data.results : [];
                if (results.length === 0) {
                    return;
                }

                var lat = Number(results[0].lat);
                var lon = Number(results[0].lon);
                if (isFinite(lat) && isFinite(lon)) {
                    setMapLocation(lat, lon, 16);
                }
            })
            .catch(function () {
                // Do not block form when map lookup fails.
            });
    }

    function validateSuggestionSelection(event) {
        if (!requireAddressSuggestion || !streetInput || !geoapifyApiKey) {
            return;
        }

        var typedAddress = streetInput.value ? streetInput.value.trim() : "";
        if (!typedAddress) {
            return;
        }

        var selectedAddressId = selectedAddressIdInput && selectedAddressIdInput.value
            ? selectedAddressIdInput.value.trim()
            : "";

        var originalStreet = originalStreetInput && originalStreetInput.value
            ? originalStreetInput.value.trim()
            : "";

        if (!selectedAddressId && typedAddress !== originalStreet) {
            event.preventDefault();
            setStatus(geoAddressStatusEl, "Please choose one address from the suggestion list.", "error");
            streetInput.focus();
        }
    }

    function fallbackManualAddress(initialProvince, initialWard, initialDistrict) {
        var provinceInput = document.createElement("input");
        provinceInput.type = "text";
        provinceInput.id = provinceSelect.id;
        provinceInput.name = provinceSelect.name;
        provinceInput.className = provinceSelect.className;
        provinceInput.required = provinceSelect.required;
        provinceInput.value = provinceSelect.value || initialProvince;

        provinceSelect.parentNode.replaceChild(provinceInput, provinceSelect);
        provinceSelect = provinceInput;

        var wardInput = document.createElement("input");
        wardInput.type = "text";
        wardInput.id = wardField.id;
        wardInput.name = wardField.name;
        wardInput.className = wardField.className;
        wardInput.required = wardField.required;
        wardInput.value = wardField.value || initialWard;

        wardField.parentNode.replaceChild(wardInput, wardField);
        wardField = wardInput;

        if (!districtField.value) {
            districtField.value = initialDistrict || wardField.value || "";
        }

        setStatus(addressApiStatusEl, "Address API unavailable. You can still type province/ward manually.", "error");
    }

    function bindSuggestionEvents() {
        streetInput.addEventListener("input", function () {
            clearSelectedAddress();

            var keyword = streetInput.value.trim();
            if (keyword.length < 1) {
                hideSuggestions();
                setStatus(geoAddressStatusEl, "Type at least 1 character to get address suggestions.", "info");
                return;
            }

            if (debounceTimer) {
                clearTimeout(debounceTimer);
            }

            debounceTimer = setTimeout(function () {
                searchSuggestions(keyword);
            }, 300);
        });

        streetInput.addEventListener("focus", function () {
            var keyword = streetInput.value.trim();
            if (keyword.length >= 1) {
                searchSuggestions(keyword);
            }
        });

        document.addEventListener("click", function (event) {
            if (!suggestionBox || suggestionBox.hidden) {
                return;
            }

            if (event.target === streetInput || suggestionBox.contains(event.target)) {
                return;
            }

            hideSuggestions();
        });
    }

    document.addEventListener("DOMContentLoaded", function () {
        form = document.querySelector("form[data-address-form='true']");
        if (!form) {
            return;
        }

        provinceSelect = document.getElementById("province");
        wardField = document.getElementById("ward");
        districtField = document.getElementById("district");
        streetInput = document.getElementById("streetAddress");
        suggestionBox = document.getElementById("streetSuggestions");
        selectedAddressIdInput = document.getElementById("selectedAddressId");
        selectedAddressLatInput = document.getElementById("selectedAddressLat");
        selectedAddressLonInput = document.getElementById("selectedAddressLon");
        originalStreetInput = document.getElementById("originalStreetAddress");
        addressApiStatusEl = document.getElementById("addressApiStatus");
        geoAddressStatusEl = document.getElementById("geoAddressStatus");

        if (!provinceSelect || !wardField || !districtField || !streetInput) {
            return;
        }

        apiUrl = form.getAttribute("data-address-api-url") || "/address-api";
        geoapifyApiKey = (form.getAttribute("data-geoapify-key") || "").trim();
        requireAddressSuggestion = form.getAttribute("data-require-address-suggestion") === "true" && !!geoapifyApiKey;

        var initialProvince = provinceSelect.getAttribute("data-selected") || "";
        var initialWard = wardField.getAttribute("data-selected") || "";
        var initialDistrict = districtField.getAttribute("data-selected") || districtField.value || "";

        initLeafletMap();

        if (geoapifyApiKey) {
            setStatus(geoAddressStatusEl, "Type at least 1 character to get address suggestions.", "info");
            locateAddressOnMap(streetInput.value ? streetInput.value.trim() : "");
        } else {
            setStatus(geoAddressStatusEl, "Geoapify API key is missing. You can still type address manually.", "info");
        }

        bindSuggestionEvents();

        provinceSelect.addEventListener("change", function () {
            var provinceCode = getSelectedCode(provinceSelect);
            loadWards(provinceCode, "")
                .then(function () {
                    syncDistrictWithWard();
                })
                .catch(function () {
                    fallbackManualAddress(initialProvince, initialWard, initialDistrict);
                });
        });

        wardField.addEventListener("change", function () {
            syncDistrictWithWard();
            clearSelectedAddress();
            hideSuggestions();
        });

        form.addEventListener("submit", function (event) {
            if (!districtField.value) {
                districtField.value = wardField.value || initialDistrict || "";
            }

            validateSuggestionSelection(event);
        });

        loadProvinces(initialProvince)
            .then(function () {
                var provinceCode = getSelectedCode(provinceSelect);

                if (initialProvince && !provinceCode) {
                    throw new Error("Cannot match province");
                }

                return loadWards(provinceCode, initialWard);
            })
            .then(function () {
                if (initialDistrict) {
                    districtField.value = initialDistrict;
                } else {
                    syncDistrictWithWard();
                }

                setStatus(addressApiStatusEl, "Latest Vietnam address data loaded.", "success");
            })
            .catch(function () {
                fallbackManualAddress(initialProvince, initialWard, initialDistrict);
            });
    });
})();
