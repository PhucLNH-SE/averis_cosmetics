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
    <input class="search" placeholder="Search..." />

    <a class="icon" href="<%=request.getContextPath()%>/cart" aria-label="Cart">
      <img class="cart-img" src="<%=request.getContextPath()%>/assets/img/Cart.png" alt="Cart">
      
      <span id="cartCount" style="color: var(--accent); font-weight: 800; margin-left: 5px; font-size: 15px;">
          ${sessionScope.cart != null ? sessionScope.cart.size() : 0}
      </span>
    </a>
    <a class="icon" href="<%=request.getContextPath()%>/profile">Login</a>
  </div>
</div>