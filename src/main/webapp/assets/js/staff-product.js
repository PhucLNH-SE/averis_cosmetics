document.addEventListener("DOMContentLoaded", function () {
    var modalElement = document.getElementById("staffProductDetailModal");
    var modalTitle = document.getElementById("staffProductDetailModalLabel");
    var modalBody = document.getElementById("staffProductDetailModalBody");

    if (!modalElement || !modalTitle || !modalBody) {
        return;
    }

    var detailModal = bootstrap.Modal.getOrCreateInstance(modalElement);
    var detailButtons = document.querySelectorAll("[data-staff-product-detail-trigger='true']");

    detailButtons.forEach(function (button) {
        button.addEventListener("click", function () {
            var detailId = button.getAttribute("data-detail-id");
            var productName = button.getAttribute("data-product-name");
            var detailTemplate = detailId ? document.getElementById(detailId) : null;

            modalTitle.textContent = productName || "Product Detail";
            modalBody.innerHTML = detailTemplate
                    ? detailTemplate.innerHTML
                    : "<p class=\"text-muted mb-0\">Product detail is unavailable.</p>";

            detailModal.show();
        });
    });

    modalElement.addEventListener("hidden.bs.modal", function () {
        modalTitle.textContent = "Product Detail";
        modalBody.innerHTML = "<p class=\"text-muted mb-0\">Select a product to view more information.</p>";
    });
});
