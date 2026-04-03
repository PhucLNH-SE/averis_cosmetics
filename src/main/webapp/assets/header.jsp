<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="topbar-shell">
  <div class="topbar">
    <div class="menu-wrap">
      <div class="menu">
        <a href="${pageContext.request.contextPath}/">Home</a>
        <div class="products-menu" id="productsMenu">
          <a href="${pageContext.request.contextPath}/products?action=list" class="products-trigger">Products</a>
          <div class="products-dropdown" id="productsDropdown">
            <div class="products-dropdown-left">
              <button type="button" class="products-tab active" data-tab="category">Category</button>
              <button type="button" class="products-tab-link" id="topSalesBtn">Top Sales</button>
              <button type="button" class="products-tab" data-tab="brand">Brand</button>
            </div>
            <div class="products-dropdown-right">
              <div class="products-panel active" id="products-panel-category"></div>
              <div class="products-panel" id="products-panel-brand"></div>
            </div>
          </div>
        </div>
        <a href="${pageContext.request.contextPath}/introduce">About Us</a>
        <a href="${pageContext.request.contextPath}/contact">Contact</a>
      </div>
    </div>

    <a class="brand" href="${pageContext.request.contextPath}/">
      <img src="${pageContext.request.contextPath}/assets/img/Logo.png" alt="Averis Logo">
      <div class="brand-name">
        <div class="brand-main">AVERIS</div>
        <div class="brand-sub">COSMETICS</div>
      </div>
    </a>

    <div class="right">
      <div class="search-trigger">
        <button type="button" class="search-toggle" id="searchToggle" aria-label="Search">
          <i class="fa-solid fa-magnifying-glass"></i>
        </button>

        <div class="search-popover" id="searchPopover">
          <div class="search-container">
            <input class="search" placeholder="Search products..." />
            <div class="search-dropdown" id="searchDropdown"></div>
          </div>
        </div>
      </div>

      <a class="icon" href="${pageContext.request.contextPath}/cart" aria-label="Cart">
        <img class="cart-img" src="${pageContext.request.contextPath}/assets/img/Cart.png" alt="Cart">
        <span id="cartCount" class="cart-count-badge">
            ${sessionScope.cart != null ? sessionScope.cart.size() : 0}
        </span>
      </a>
      <c:choose>
        <c:when test="${not empty sessionScope.customer}">
          <a class="icon" href="${pageContext.request.contextPath}/profile?action=view">Welcome, ${sessionScope.customer.username}!</a>
          <a class="icon" href="${pageContext.request.contextPath}/logout">Logout</a>
        </c:when>
        <c:otherwise>
          <a class="icon" href="${pageContext.request.contextPath}/auth">Register/Login</a>
        </c:otherwise>
      </c:choose>
    </div>

  </div>
</div>

<script>
  document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.querySelector('.search');
    const searchDropdown = document.getElementById('searchDropdown');
    const searchToggle = document.getElementById('searchToggle');
    const searchPopover = document.getElementById('searchPopover');
    const productsMenu = document.getElementById('productsMenu');
    const productsDropdown = document.getElementById('productsDropdown');
    const categoryPanel = document.getElementById('products-panel-category');
    const brandPanel = document.getElementById('products-panel-brand');
    const topSalesBtn = document.getElementById('topSalesBtn');
    const productTabs = document.querySelectorAll('.products-tab');
    let debounceTimer;

    if (productsMenu && productsDropdown && categoryPanel && brandPanel) {
      loadHeaderMenuData();
      productTabs.forEach(tab => {
        tab.addEventListener('mouseenter', function () {
          setProductsTab(this.dataset.tab);
        });
      });

      let closeTimer;
      productsMenu.addEventListener('mouseenter', function () {
        clearTimeout(closeTimer);
        productsMenu.classList.add('open');
      });
      productsMenu.addEventListener('mouseleave', function () {
        closeTimer = setTimeout(() => {
          productsMenu.classList.remove('open');
        }, 180);
      });
      productsDropdown.addEventListener('mouseenter', function () {
        clearTimeout(closeTimer);
        productsMenu.classList.add('open');
      });
      productsDropdown.addEventListener('mouseleave', function () {
        closeTimer = setTimeout(() => {
          productsMenu.classList.remove('open');
        }, 180);
      });
    }

    if (topSalesBtn) {
      topSalesBtn.addEventListener('click', function () {
        window.location.href = '${pageContext.request.contextPath}/products?action=topSales';
      });
    }

    if (searchInput && searchDropdown && searchToggle && searchPopover) {
      searchToggle.addEventListener('click', function (event) {
        event.stopPropagation();
        searchPopover.classList.toggle('show');
        if (searchPopover.classList.contains('show')) {
          searchInput.focus();
        }
      });

      document.addEventListener('click', function (event) {
        const clickedInside = searchPopover.contains(event.target) || searchToggle.contains(event.target);
        if (!clickedInside) {
          searchPopover.classList.remove('show');
          searchDropdown.style.display = 'none';
        }
      });

      searchInput.addEventListener('input', function() {
        clearTimeout(debounceTimer);
        const keyword = this.value.trim();

        if (keyword === '') {
          searchDropdown.style.display = 'none';
          return;
        }

        debounceTimer = setTimeout(() => {
          fetchSuggestions(keyword);
        }, 300);
      });

      searchInput.addEventListener('focus', function() {
        if (this.value.trim() !== '') {
          searchDropdown.style.display = 'block';
        }
      });

      searchInput.addEventListener('blur', function() {
        setTimeout(() => {
          searchDropdown.style.display = 'none';
        }, 200);
      });

      searchInput.addEventListener('keydown', function(event) {
        if (event.key !== 'Enter') {
          return;
        }

        event.preventDefault();
        const keyword = this.value.trim();

        if (keyword === '') {
          window.location.href = '${pageContext.request.contextPath}/products?action=list';
          return;
        }

        window.location.href = '${pageContext.request.contextPath}/products?action=filter&keyword=' + encodeURIComponent(keyword);
      });
    }

    function loadHeaderMenuData() {
      fetch('${pageContext.request.contextPath}/header-menu-data')
        .then(response => response.json())
        .then(data => {
          renderFilterPanel(categoryPanel, data.categories || [], 'category');
          renderFilterPanel(brandPanel, data.brands || [], 'brand');
          setProductsTab('category');
        })
        .catch(error => {
          console.error('Error loading header menu:', error);
        });
    }

    function renderFilterPanel(panel, items, type) {
      panel.innerHTML = '';
      if (!items || items.length === 0) {
        const empty = document.createElement('div');
        empty.className = 'products-filter-link';
        empty.textContent = 'No data';
        panel.appendChild(empty);
        return;
      }

      items.forEach(item => {
        const link = document.createElement('a');
        link.className = 'products-filter-link';
        link.textContent = item.name;
        link.href = '${pageContext.request.contextPath}/products?action=filter&' + type + '=' + encodeURIComponent(item.name);
        panel.appendChild(link);
      });
    }

    function setProductsTab(tabName) {
      productTabs.forEach(tab => {
        if (tab.dataset.tab === tabName) {
          tab.classList.add('active');
        } else {
          tab.classList.remove('active');
        }
      });

      if (tabName === 'brand') {
        brandPanel.classList.add('active');
        categoryPanel.classList.remove('active');
      } else {
        categoryPanel.classList.add('active');
        brandPanel.classList.remove('active');
      }
    }

    function fetchSuggestions(keyword) {
      fetch('${pageContext.request.contextPath}/products/suggest?keyword=' + encodeURIComponent(keyword), {
        headers: {
          'X-Requested-With': 'XMLHttpRequest'
        }
      })
        .then(response => response.json())
        .then(data => {
          displaySuggestions(data);
        })
        .catch(error => {
          console.error('Error fetching suggestions:', error);
        });
    }

    function displaySuggestions(products) {
      searchDropdown.innerHTML = '';

      if (!products || products.length === 0) {
        const noResults = document.createElement('div');
        noResults.className = 'search-item';
        noResults.textContent = 'No products found';
        searchDropdown.appendChild(noResults);
        searchDropdown.style.display = 'block';
        return;
      }

      products.forEach(product => {
        const item = document.createElement('a');
        item.className = 'search-item';
        item.href = '${pageContext.request.contextPath}/products?action=detail&id=' + product.productId;

        let imageUrl = '${pageContext.request.contextPath}/assets/img/default-product.jpg';
        let targetImage = product.mainImage || (product.images && product.images.length > 0 ? product.images[0].image : null);

        if (targetImage) {
          let folder = targetImage.includes('-') ? 'products/' : '';
          imageUrl = '${pageContext.request.contextPath}/assets/img/' + folder + targetImage;
        }

        item.innerHTML = '<div class="search-item-image">' +
                           '<img src="' + imageUrl + '" alt="' + product.name + '" onerror="this.src=\'${pageContext.request.contextPath}/assets/img/default-product.jpg\';">' +
                         '</div>' +
                         '<div class="search-item-info">' +
                           '<div class="search-item-name">' + product.name + '</div>' +
                           '<div class="search-item-brand">' + (product.brand ? product.brand.name : 'Unknown Brand') + '</div>' +
                         '</div>';

        searchDropdown.appendChild(item);
      });

      searchDropdown.style.display = 'block';
    }
    
    // Call the server to get the live cart count from the session
    function updateCartCountRealtime() {
        const cartCountEl = document.getElementById('cartCount');
        if (!cartCountEl) return;

        fetch('${pageContext.request.contextPath}/cart?action=getCount')
            .then(response => response.text())
            .then(count => {
                cartCountEl.innerText = count.trim();
            })
            .catch(err => console.error('Failed to sync cart count:', err));
    }

    // Listen for page show events (including Back navigation)
    window.addEventListener('pageshow', function(event) {
        updateCartCountRealtime();
    });
    
  });
</script>



















