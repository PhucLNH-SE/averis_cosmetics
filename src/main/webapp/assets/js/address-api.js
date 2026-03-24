(function () {
    "use strict";

    var form = null;
    var provinceField = null;
    var districtField = null;
    var wardField = null;
    var addressApiStatusEl = null;

    var apiUrl = "/address-api";

    function normalize(value) {
        return (value || "").trim().toLowerCase();
    }

    function setStatus(message, type) {
        if (!addressApiStatusEl) {
            return;
        }

        addressApiStatusEl.textContent = message || "";
        addressApiStatusEl.classList.remove("text-muted", "text-danger", "text-success");

        if (type === "error") {
            addressApiStatusEl.classList.add("text-danger");
        } else if (type === "success") {
            addressApiStatusEl.classList.add("text-success");
        } else {
            addressApiStatusEl.classList.add("text-muted");
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
        if (!select || select.tagName !== "SELECT") {
            return;
        }

        select.innerHTML = "";

        var placeholderOption = document.createElement("option");
        placeholderOption.value = "";
        placeholderOption.textContent = placeholder;
        placeholderOption.selected = true;
        select.appendChild(placeholderOption);

        select.disabled = !!disableSelect;
    }

    function populateSelect(select, items, selectedName, placeholder) {
        if (!select || select.tagName !== "SELECT") {
            return;
        }

        var normalizedSelected = normalize(selectedName);
        var matched = false;

        resetSelect(select, placeholder, false);

        for (var i = 0; i < items.length; i++) {
            var item = items[i] || {};
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
        if (!select || select.tagName !== "SELECT") {
            return "";
        }

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

    function loadProvinces(selectedProvince) {
        setStatus("Loading latest Vietnam address data...");
        resetSelect(provinceField, "Loading provinces...", true);
        resetSelect(districtField, "Select district", true);
        resetSelect(wardField, "Select ward", true);

        return fetchAddressData("provinces").then(function (data) {
            populateSelect(provinceField, Array.isArray(data) ? data : [], selectedProvince, "Select province/city");
        });
    }

    function loadDistricts(provinceCode, selectedDistrict) {
        resetSelect(districtField, "Loading districts...", true);
        resetSelect(wardField, "Select ward", true);

        if (!provinceCode) {
            resetSelect(districtField, "Select district", true);
            return Promise.resolve();
        }

        return fetchAddressData("districts", {provinceCode: provinceCode}).then(function (data) {
            var districts = data && Array.isArray(data.districts) ? data.districts : [];
            populateSelect(districtField, districts, selectedDistrict, "Select district");
        });
    }

    function loadWards(districtCode, selectedWard) {
        resetSelect(wardField, "Loading wards...", true);

        if (!districtCode) {
            resetSelect(wardField, "Select ward", true);
            return Promise.resolve();
        }

        return fetchAddressData("wards", {districtCode: districtCode}).then(function (data) {
            var wards = data && Array.isArray(data.wards) ? data.wards : [];
            populateSelect(wardField, wards, selectedWard, "Select ward");
        });
    }

    function replaceSelectWithInput(select, initialValue, placeholder) {
        var input = document.createElement("input");
        input.type = "text";
        input.id = select.id;
        input.name = select.name;
        input.className = select.className;
        input.required = select.required;
        input.value = initialValue || select.getAttribute("data-selected") || "";
        input.placeholder = placeholder;

        select.parentNode.replaceChild(input, select);
        return input;
    }

    function fallbackManual(initialProvince, initialDistrict, initialWard) {
        if (provinceField && provinceField.tagName === "SELECT") {
            provinceField = replaceSelectWithInput(provinceField, initialProvince, "Enter province/city");
        }
        if (districtField && districtField.tagName === "SELECT") {
            districtField = replaceSelectWithInput(districtField, initialDistrict, "Enter district");
        }
        if (wardField && wardField.tagName === "SELECT") {
            wardField = replaceSelectWithInput(wardField, initialWard, "Enter ward");
        }

        setStatus("Address API unavailable. You can type province, district and ward manually.", "error");
    }

    document.addEventListener("DOMContentLoaded", function () {
        form = document.querySelector("form[data-address-form='true']");
        if (!form) {
            return;
        }

        provinceField = document.getElementById("province");
        districtField = document.getElementById("district");
        wardField = document.getElementById("ward");
        addressApiStatusEl = document.getElementById("addressApiStatus");

        if (!provinceField || !districtField || !wardField) {
            return;
        }

        apiUrl = form.getAttribute("data-address-api-url") || "/address-api";

        var initialProvince = provinceField.getAttribute("data-selected") || "";
        var initialDistrict = districtField.getAttribute("data-selected") || "";
        var initialWard = wardField.getAttribute("data-selected") || "";

        provinceField.addEventListener("change", function () {
            if (provinceField.tagName !== "SELECT") {
                return;
            }

            loadDistricts(getSelectedCode(provinceField), "")
                .catch(function () {
                    fallbackManual(initialProvince, initialDistrict, initialWard);
                });
        });

        districtField.addEventListener("change", function () {
            if (districtField.tagName !== "SELECT") {
                return;
            }

            loadWards(getSelectedCode(districtField), "")
                .catch(function () {
                    fallbackManual(initialProvince, initialDistrict, initialWard);
                });
        });

        loadProvinces(initialProvince)
            .then(function () {
                var provinceCode = getSelectedCode(provinceField);

                if (initialProvince && !provinceCode) {
                    throw new Error("Cannot match province");
                }

                return loadDistricts(provinceCode, initialDistrict);
            })
            .then(function () {
                var districtCode = getSelectedCode(districtField);

                if (initialDistrict && !districtCode) {
                    throw new Error("Cannot match district");
                }

                return loadWards(districtCode, initialWard);
            })
            .then(function () {
                setStatus("Latest Vietnam address data loaded.", "success");
            })
            .catch(function () {
                fallbackManual(initialProvince, initialDistrict, initialWard);
            });
    });
})();
