<%@ page pageEncoding="UTF-8" %>
<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
<style>
  :root {
    --bg: #fff;
    --surface: #faf8f5;
    --border: #e9e2d8;
    --text: #1f2937;
    --muted: #6b7280;
    --accent: #b45309;
    --accent-hover: #92400e;
  }

  body {
    background: var(--bg);
    margin: 0;
    font-family: Arial, sans-serif;
    color: var(--text);
  }

  .topbar-shell {
    background: var(--surface);
    border-bottom: 1px solid var(--border);
    box-shadow: 0 8px 22px rgba(31, 41, 55, .06);
    position: sticky;
    top: 0;
    z-index: 1400;
  }

  .topbar {
    width: 100%;
    box-sizing: border-box;
    height: 74px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 24px;
    position: relative;
  }

  .menu-wrap,
  .right {
    width: 390px;
    min-width: 390px;
    display: flex;
    align-items: center;
  }

  .menu-wrap {
    justify-content: flex-start;
  }

  .menu {
    display: flex;
    gap: 22px;
    font-weight: 700;
    text-transform: uppercase;
    font-size: 14px;
  }

  .menu a {
    color: var(--text);
    text-decoration: none;
    position: relative;
    padding: 6px 2px;
  }

  .menu a:hover {
    color: var(--accent);
  }

  .menu a::after {
    content: "";
    position: absolute;
    left: 0;
    right: 0;
    bottom: 0;
    height: 2px;
    background: transparent;
    border-radius: 2px;
    transform: scaleX(0);
    transition: transform .18s ease, background .18s ease;
  }

  .menu a:hover::after {
    background: var(--accent);
    transform: scaleX(1);
  }

  .products-menu {
    position: relative;
    display: inline-flex;
    align-items: center;
  }

  .products-trigger {
    display: inline-flex;
    align-items: center;
  }

  .products-dropdown {
    position: absolute;
    top: 100%;
    left: 0;
    min-width: 560px;
    background: #fff;
    border: 1px solid var(--border);
    border-radius: 14px;
    box-shadow: 0 18px 40px rgba(15, 23, 42, 0.16);
    display: none;
    overflow: hidden;
    z-index: 1300;
  }

  .products-menu:hover .products-dropdown,
  .products-menu.open .products-dropdown {
    display: flex;
  }

  .products-dropdown-left {
    width: 180px;
    border-right: 1px solid #ebe7df;
    background: #fbfaf7;
    padding: 8px;
    display: flex;
    flex-direction: column;
    gap: 6px;
  }

  .products-tab,
  .products-tab-link {
    display: flex;
    align-items: center;
    width: 100%;
    box-sizing: border-box;
    min-height: 40px;
    border: none;
    background: transparent;
    text-align: left;
    border-radius: 10px;
    padding: 10px 12px;
    font-size: 13px;
    font-weight: 700;
    color: #334155;
    text-transform: uppercase;
    letter-spacing: .3px;
    text-decoration: none;
    cursor: pointer;
    font-family: inherit;
    line-height: 1;
  }

  .products-tab:hover,
  .products-tab-link:hover,
  .products-tab.active {
    background: #efe8dc;
    color: var(--accent);
  }

  .products-dropdown-right {
    width: 380px;
    max-height: 340px;
    overflow-y: auto;
    padding: 10px;
  }

  .products-panel {
    display: none;
    grid-template-columns: 1fr;
    gap: 6px;
  }

  .products-panel.active {
    display: grid;
  }

  .products-filter-link {
    padding: 9px 10px;
    border-radius: 8px;
    text-decoration: none;
    color: #1f2937;
    font-size: 14px;
    font-weight: 600;
    border: 1px solid transparent;
  }

  .products-filter-link:hover {
    border-color: #e8dccb;
    background: #fff8f0;
    color: var(--accent);
  }

  .brand {
    text-decoration: none;
    display: flex;
    align-items: center;
    gap: 10px;
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
  }

  .brand img {
    width: 48px;
    height: 48px;
    object-fit: contain;
    display: block;
    flex-shrink: 0;
  }

  .brand-name {
    font-size: 18px;
    letter-spacing: 3px;
    font-weight: 800;
    line-height: 1.2;
    color: var(--text);
    text-transform: uppercase;
  }

  .right {
    justify-content: flex-end;
    gap: 10px;
  }

  .search-trigger {
    position: relative;
    display: inline-flex;
    align-items: center;
  }

  .search-toggle {
    border: 1px solid var(--border);
    background: #fff;
    color: var(--text);
    width: 36px;
    height: 36px;
    border-radius: 10px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: background .15s ease, color .15s ease;
  }

  .search-toggle:hover {
    background: rgba(180, 83, 9, .10);
    color: var(--accent);
  }

  .icon {
    color: var(--text);
    text-decoration: none;
    font-size: 14px;
    padding: 6px 8px;
    border-radius: 10px;
    transition: background .15s ease, color .15s ease;
    display: inline-flex;
    align-items: center;
    gap: 6px;
    white-space: nowrap;
  }

  .icon:hover {
    background: rgba(180, 83, 9, .10);
    color: var(--accent);
  }

  .cart-img {
    width: 40px;
    height: 30px;
    display: block;
  }

        .search-popover {
    position: absolute;
    top: calc(100% + 8px);
    left: 0;
    width: 300px;
    background: #fff;
    border: 1px solid var(--border);
    border-radius: 12px;
    box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15);
    padding: 10px;
    z-index: 1100;
    display: none;
  }
.search-popover.show {
    display: block;
  }

  .search-container {
    position: relative;
    display: block;
    width: 100%;
  }

  .search {
    background: #fff;
    border: 1px solid var(--border);
    color: var(--text);
    outline: none;
    padding: 10px 12px;
    width: 100%;
    box-sizing: border-box;
    border-radius: 10px;
  }

  .search::placeholder {
    color: var(--muted);
  }

  .search:focus {
    border-color: var(--accent);
    box-shadow: 0 0 0 3px rgba(180, 83, 9, .12);
  }

  .search-dropdown {
    position: absolute;
    top: calc(100% + 8px);
    left: 0;
    width: 100%;
    background: white;
    border: 1px solid var(--border);
    border-radius: 12px;
    box-shadow: 0 12px 30px rgba(0,0,0,0.15);
    z-index: 1200;
    max-height: min(60vh, 520px);
    min-height: 160px;
    overflow-y: auto;
    overflow-x: hidden;
    display: none;
  }

  .search-dropdown::-webkit-scrollbar {
    width: 6px;
  }

  .search-dropdown::-webkit-scrollbar-track {
    background: var(--surface);
    border-radius: 3px;
  }

  .search-dropdown::-webkit-scrollbar-thumb {
    background: var(--accent);
    border-radius: 3px;
  }

  .search-dropdown::-webkit-scrollbar-thumb:hover {
    background: var(--accent-hover);
  }

  .search-item {
    width: 100%;
    box-sizing: border-box;
    padding: 14px 16px;
    cursor: pointer;
    border-bottom: 1px solid var(--border);
    text-decoration: none;
    color: var(--text);
    display: flex;
    align-items: center;
    gap: 12px;
    transition: all 0.2s ease;
  }

  .search-item:last-child {
    border-bottom: none;
    border-radius: 0 0 12px 12px;
  }

  .search-item:hover {
    background: linear-gradient(90deg, var(--surface), #fefefe);
    transform: translateX(4px);
  }

  .search-item-image {
    width: 55px;
    height: 55px;
    object-fit: cover;
    border-radius: 10px;
    border: 1px solid var(--border);
    flex-shrink: 0;
    background: var(--surface);
    overflow: hidden;
  }

  .search-item-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .search-item-info {
    flex: 1;
    overflow: hidden;
  }

  .search-item-name {
    font-weight: 600;
    font-size: 15px;
    margin: 0 0 3px 0;
    color: var(--text);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    line-height: 1.4;
  }

  .search-item-brand {
    font-size: 13px;
    color: var(--muted);
    margin: 0;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    font-weight: 500;
  }
@media (max-width: 1200px) {
    .menu-wrap,
    .right {
      width: 340px;
      min-width: 340px;
    }

    .menu {
      gap: 14px;
      font-size: 13px;
    }

    .brand-name {
      font-size: 15px;
      letter-spacing: 2px;
    }

      .search-popover {
    position: absolute;
    top: calc(100% + 8px);
    left: 0;
    width: 300px;
    background: #fff;
    border: 1px solid var(--border);
    border-radius: 12px;
    box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15);
    padding: 10px;
    z-index: 1100;
    display: none;
  }
.products-dropdown {
      min-width: 500px;
    }
    .products-dropdown-right {
      width: 320px;
    }
  }

  @media (max-width: 980px) {
    .topbar {
      height: auto;
      min-height: 70px;
      flex-wrap: wrap;
      row-gap: 10px;
      padding: 10px 16px;
    }

    .brand {
      position: static;
      transform: none;
      order: -1;
      width: 100%;
      justify-content: center;
    }

    .menu-wrap,
    .right {
      width: 100%;
      min-width: 0;
    }

    .menu {
      justify-content: center;
      flex-wrap: wrap;
      width: 100%;
    }

    .right {
      justify-content: center;
      flex-wrap: wrap;
    }

      .search-popover {
    position: absolute;
    top: calc(100% + 8px);
    left: 0;
    width: 300px;
    background: #fff;
    border: 1px solid var(--border);
    border-radius: 12px;
    box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15);
    padding: 10px;
    z-index: 1100;
    display: none;
  }
.products-dropdown {
      left: 50%;
      transform: translateX(-50%);
      min-width: min(92vw, 560px);
    }
  }
</style>

<div class="topbar-shell">
  <div class="topbar">
    <div class="menu-wrap">
      <div class="menu">
        <a href="<%=request.getContextPath()%>/">Home</a>
        <div class="products-menu" id="productsMenu">
          <a href="<%=request.getContextPath()%>/products" class="products-trigger">Products</a>
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
        <a href="<%=request.getContextPath()%>/introduce">About Us</a>
        <a href="<%=request.getContextPath()%>/contact">Contact</a>
      </div>
    </div>

    <a class="brand" href="<%=request.getContextPath()%>/">
      <img src="<%=request.getContextPath()%>/assets/img/Logo.png" alt="Averis Logo">
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

      <a class="icon" href="<%=request.getContextPath()%>/cart" aria-label="Cart">
        <img class="cart-img" src="<%=request.getContextPath()%>/assets/img/Cart.png" alt="Cart">
        <span id="cartCount" style="color: var(--accent); font-weight: 800; margin-left: 5px; font-size: 15px;">
            ${sessionScope.cart != null ? sessionScope.cart.size() : 0}
        </span>
      </a>

      <% 
          Object customerObj = session.getAttribute("customer"); 
          if (customerObj != null) { 
      %>
          <a class="icon" href="<%=request.getContextPath()%>/profile?action=view">Welcome, <%= ((Model.Customer)customerObj).getUsername() %>!</a>
          <a class="icon" href="<%=request.getContextPath()%>/logout">Logout</a>
      <% 
          } else { 
      %>
          <a class="icon" href="<%=request.getContextPath()%>/auth">Register/Login</a>
      <% 
          } 
      %>
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
        window.location.href = '<%=request.getContextPath()%>/products?sort=top_sales';
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
          window.location.href = '<%=request.getContextPath()%>/products';
          return;
        }

        window.location.href = '<%=request.getContextPath()%>/products?keyword=' + encodeURIComponent(keyword);
      });
    }

    function loadHeaderMenuData() {
      fetch('<%=request.getContextPath()%>/header-menu-data')
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
        link.href = '<%=request.getContextPath()%>/products?' + type + '=' + encodeURIComponent(item.name);
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
      fetch('<%=request.getContextPath()%>/products/suggest?keyword=' + encodeURIComponent(keyword), {
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
        item.href = '<%=request.getContextPath()%>/products?id=' + product.productId;

        let imageUrl = '<%=request.getContextPath()%>/assets/img/default-product.jpg';
        let targetImage = product.mainImage || (product.images && product.images.length > 0 ? product.images[0].image : null);

        if (targetImage) {
          let folder = targetImage.includes('-') ? 'products/' : '';
          imageUrl = '<%=request.getContextPath()%>/assets/img/' + folder + targetImage;
        }

        item.innerHTML = '<div class="search-item-image">' +
                           '<img src="' + imageUrl + '" alt="' + product.name + '" onerror="this.src=\'<%=request.getContextPath()%>/assets/img/default-product.jpg\';">' +
                         '</div>' +
                         '<div class="search-item-info">' +
                           '<div class="search-item-name">' + product.name + '</div>' +
                           '<div class="search-item-brand">' + (product.brand ? product.brand.name : 'Unknown Brand') + '</div>' +
                         '</div>';

        searchDropdown.appendChild(item);
      });

      searchDropdown.style.display = 'block';
    }
    
    // H�m g?i l�n Server d? l?y s? lu?ng gi? h�ng th?c t? trong Session
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

    // L?ng nghe s? ki?n hi?n th? trang (ch?y c? khi nh?n n�t Back)
    window.addEventListener('pageshow', function(event) {
        updateCartCountRealtime();
    });
    
  });
</script>
















