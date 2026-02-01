<style>
  :root{
    --bg: #fff;
    --surface: #faf8f5;
    --border: #e9e2d8;
    --text: #1f2937;
    --muted: #6b7280;
    --accent: #b45309;
    --accent-hover:#92400e;
  }

  body{
    background: var(--bg);
    margin:0;
    font-family: Arial, sans-serif;
    color: var(--text);
  }

  .topbar{
    background: var(--surface);
    height:70px;
    display:flex;
    align-items:center;
    justify-content:space-between;
    padding:0 24px;
    border-bottom: 1px solid var(--border);
    box-shadow: 0 8px 22px rgba(31,41,55,.06);
  }

  /* ? g?p logo + ch? */
  .brand{
        text-decoration: none;
    display:flex;
    align-items:center;
    gap:10px;            /* ch?nh 6px n?u mu?n sát h?n */
    min-width: 220px;    /* gi? kh?i trái ?n ??nh */
  }

  .brand img{
    width:55px;          /* logo nh? l?i cho cân */
    height:55px;
    object-fit:contain;
    display:block;
  }

  .brand-name{
    font-size:42px;
    letter-spacing:6px;
    font-weight:800;
    line-height:1;
    color: var(--text);
    text-transform: uppercase;
  }

  .menu{
    display:flex;
    gap:22px;
    font-weight:700;
    text-transform:uppercase;
    font-size:14px;
  }

  .menu a{
    color: var(--text);
    text-decoration:none;
    position:relative;
    padding:6px 2px;
  }
  .menu a:hover{ color: var(--accent); }
  .menu a::after{
    content:"";
    position:absolute;
    left:0; right:0; bottom:0;
    height:2px;
    background: transparent;
    border-radius: 2px;
    transform: scaleX(0);
    transition: transform .18s ease, background .18s ease;
  }
  .menu a:hover::after{
    background: var(--accent);
    transform: scaleX(1);
  }

  .right{
    display:flex;
    align-items:center;
    gap:12px;
    min-width: 260px;   /* gi? kh?i ph?i ?n ??nh */
    justify-content:flex-end;
  }

  .search{
    background: transparent;
    border: none;
    border-bottom: 1px solid var(--border);
    color: var(--text);
    outline: none;
    padding:6px 8px;
    width:180px;
  }
  .search::placeholder{ color: var(--muted); }
  .search:focus{ border-bottom-color: var(--accent); }

  .icon{
    color: var(--text);
    text-decoration:none;
    font-size:14px;
    padding:6px 8px;
    border-radius: 10px;
    transition: background .15s ease, color .15s ease;
    display:inline-flex;
    align-items:center;
    gap:6px;
  }
  .icon:hover{
    background: rgba(180,83,9,.10);
    color: var(--accent);
  }

  .cart-img{
    width:22px;        /* ? cart icon v?a ph?i */
    height:22px;
    display:block;
  }
</style>

<div class="topbar">
  <!-- ? Brand: logo + AVERIS g?n nhau -->
  <a class="brand" href="<%=request.getContextPath()%>/">
    <img src="<%=request.getContextPath()%>/assets/img/Logo.png" alt="Averis Logo">
    <span class="brand-name">AVERIS</span>
  </a>

  <div class="menu">
    <a href="<%=request.getContextPath()%>/">Home</a>
    <a href="<%=request.getContextPath()%>/products">Product</a>
    <a href="<%=request.getContextPath()%>/introduce">Introduce</a>
    <a href="<%=request.getContextPath()%>/contact">Contact</a>
  </div>

  <div class="right">
    <input class="search" placeholder="Search..." />

    <a class="icon" href="<%=request.getContextPath()%>/cart" aria-label="Cart">
      <img class="cart-img" src="<%=request.getContextPath()%>/assets/img/Cart.png" alt="Cart">
    </a>

    <a class="icon" href="<%=request.getContextPath()%>/profile">Profile</a>
  </div>
</div>
