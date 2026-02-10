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

  .topbar {
    background: var(--surface);
    height: 70px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 24px;
    border-bottom: 1px solid var(--border);
    box-shadow: 0 8px 22px rgba(31, 41, 55, .06);
  }

  /* Brand section with logo */
  .brand {
    text-decoration: none;
    display: flex;
    align-items: center;
    gap: 10px;
    /* adjust 6px if want closer */
    min-width: 220px;
    /* keep from shifting to fixed */
  }

  .brand img {
    width: 55px;
    /* logo smaller to fit */
    height: 55px;
    object-fit: contain;
    display: block;
  }

  .brand-name {
    font-size: 42px;
    letter-spacing: 6px;
    font-weight: 800;
    line-height: 1;
    color: var(--text);
    text-transform: uppercase;
  }

  .menu {
    display: flex;
    gap: 22px;
    font-weight: 700;
    text-transform: uppercase;
    font-size: 14px;
    MARGIN-LEFT: 57PX;
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

  .right {
    display: flex;
    align-items: center;
    gap: 12px;
    min-width: 260px;
    /* keep from shifting to fixed */
    justify-content: flex-end;
  }

  .search {
    background: transparent;
    border: none;
    border-bottom: 1px solid var(--border);
    color: var(--text);
    outline: none;
    padding: 6px 8px;
    width: 180px;
  }

  .search::placeholder {
    color: var(--muted);
  }

  .search:focus {
    border-bottom-color: var(--accent);
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
  }

  .icon:hover {
    background: rgba(180, 83, 9, .10);
    color: var(--accent);
  }

  .cart-img {
    width: 40px;
    /* for cart icon spacing */
    height: 30px;
    display: block;
  }
</style>

<!-- Search dropdown styles -->
<style>
.search-container {
  position: relative;
  display: inline-block;
}

.search-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  width: 350px;
  background: white;
  border: 1px solid var(--border);
  border-radius: 12px;
  box-shadow: 0 12px 30px rgba(0,0,0,0.15);
  z-index: 1000;
  max-height: 400px;
  overflow-y: auto;
  display: none;
  margin-top: 8px;
  backdrop-filter: blur(10px);
}

/* Scrollbar styling */
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
  padding: 14px 16px;
  cursor: pointer;
  border-bottom: 1px solid var(--border);
  text-decoration: none;
  color: var(--text);
  display: flex;
  align-items: center;
  gap: 12px;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.search-item:last-child {
  border-bottom: none;
  border-radius: 0 0 12px 12px;
}

.search-item:hover {
  background: linear-gradient(90deg, var(--surface), #fefefe);
  transform: translateX(6px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.search-item-image {
  width: 55px;
  height: 55px;
  object-fit: cover;
  border-radius: 10px;
  border: 1px solid var(--border);
  flex-shrink: 0;
  background: var(--surface);
  display: flex;
  align-items: center;
  justify-content: center;
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

.search-no-results {
  padding: 24px;
  text-align: center;
  color: var(--muted);
  font-style: italic;
  font-size: 14px;
}
</style>

<div class="topbar">
  <!-- Brand: logo + AVERIS together -->
  <a class="brand" href="<%=request.getContextPath()%>/">
    <img src="<%=request.getContextPath()%>/assets/img/Logo.png" alt="Averis Logo">
    <div class="brand-name">
      <div class="brand-main">AVERIS</div>
      <div class="brand-sub">COSMETICS</div>
    </div>
  </a>

  <div class="menu">
    <a href="<%=request.getContextPath()%>/">Home</a>
    <a href="<%=request.getContextPath()%>/products">Products</a>
    <a href="<%=request.getContextPath()%>/introduce">About Us</a>
    <a href="<%=request.getContextPath()%>/contact">Contact</a>
  </div>

  <div class="right">
    <div class="search-container">
      <input class="search" placeholder="Search products..." />
      <div class="search-dropdown" id="searchDropdown"></div>
    </div>

    <a class="icon" href="<%=request.getContextPath()%>/cart" aria-label="Cart">
      <img class="cart-img" src="<%=request.getContextPath()%>/assets/img/Cart.png" alt="Cart">
    </a>

    <% 
        Object customerObj = session.getAttribute("customer"); 
        if (customerObj != null) { 
    %>
        <a class="icon" href="<%=request.getContextPath()%>/profile">Welcome, <%= ((Model.Customer)customerObj).getUsername() %>!</a>
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

<script>
  document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.querySelector('.search');
    const searchDropdown = document.getElementById('searchDropdown');
    let debounceTimer;
    
    searchInput.addEventListener('input', function() {
      clearTimeout(debounceTimer);
      const keyword = this.value.trim();
      
      if (keyword === '') {
        searchDropdown.style.display = 'none';
        return;
      }
      
      debounceTimer = setTimeout(() => {
        fetchSuggestions(keyword);
      }, 300); // Debounce delay of 300ms
    });
    
    searchInput.addEventListener('focus', function() {
      if (this.value.trim() !== '') {
        searchDropdown.style.display = 'block';
      }
    });
    
    searchInput.addEventListener('blur', function() {
      // Delay hiding to allow click on suggestions
      setTimeout(() => {
        searchDropdown.style.display = 'none';
      }, 200);
    });
    
    function fetchSuggestions(keyword) {
      // Set a custom header to identify AJAX request
      fetch('<%=request.getContextPath()%>/products?keyword=' + encodeURIComponent(keyword), {
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
        
        let imageUrl = '/assets/img/default-product.jpg'; // Default image
        if (product.mainImage) {
          imageUrl = '<%=request.getContextPath()%>/assets/img/' + product.mainImage;
        } else if (product.images && product.images.length > 0 && product.images[0].image) {
          imageUrl = '<%=request.getContextPath()%>/assets/img/' + product.images[0].image;
        }
        
        item.innerHTML = '<img src="' + imageUrl + '" alt="' + product.name + '" class="search-item-image" onerror="this.src=\'' + '<%=request.getContextPath()%>/assets/img/default-product.jpg' + '\';">' +
                         '<div class="search-item-info">' +
                         '<div class="search-item-name">' + product.name + '</div>' +
                         '<div class="search-item-brand">' + (product.brand ? product.brand.name : 'Unknown Brand') + '</div>' +
                         '</div>';
        
        searchDropdown.appendChild(item);
      });
      
      searchDropdown.style.display = 'block';
    }
  });
</script>