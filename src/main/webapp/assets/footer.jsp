<style>
  .footer{
    background:#faf8f5;
    color:#111;            /* TEXT COLOR */
    padding:40px 0;
    font-family: Arial, sans-serif;
  }

  .footer .wrap{
    width:92%;
    max-width:1200px;
    margin:0 auto;
    display:grid;
    grid-template-columns: 1.2fr 1fr 1fr 1fr;
    gap:28px;
  }

  .f-title{
    font-weight:700;
    text-transform:uppercase;
    margin-bottom:12px;
    font-size:14px;
    letter-spacing:.5px;
    color:#111;            /* title color */
  }

  .f-text{
    color:#111;            /* content color */
    font-size:13px;
    line-height:1.9;
  }

  .footer a{
    color:#111;            /* link color */
    font-size:13px;
    line-height:1.9;
    text-decoration:none;
    transition: color .15s ease;
  }
  .footer a:hover{
    color:#b45309;         /* hover orange slightly (change if wanted) */
  }

  .footer ul{list-style:none; padding:0; margin:0;}
  .footer li{margin:6px 0;}

  .line{
    width:92%;
    max-width:1200px;
    margin:22px auto 0;
    border-top:1px solid rgba(0,0,0,.12);  /* light line */
    padding-top:12px;
    color:rgba(0,0,0,.65);                 /* line text dark slightly */
    font-size:12px;
    display:flex;
    justify-content:space-between;
    flex-wrap:wrap;
    gap:10px;
  }

  @media(max-width:900px){ .footer .wrap{grid-template-columns:1fr 1fr;} }
  @media(max-width:520px){ .footer .wrap{grid-template-columns:1fr;} }
</style>
<footer class="footer">
  <div class="wrap">
    <div>
      <div class="f-title">AVERIS COSMETICS</div>
      <div class="f-text">
        Averis Cosmetics Business<br>
        Hotline: 0966434787<br>
        Email: support@averis.com
      </div>
    </div>

    <div>
      <div class="f-title">Support</div>
      <ul>
        <li><a href="#">Privacy Policy</a></li>
        <li><a href="#">Shipping</a></li>
        <li><a href="#">Returns</a></li>
      </ul>
    </div>

    <div>
      <div class="f-title">Guides</div>
      <ul>
        <li><a href="#">How to Buy</a></li>
        <li><a href="#">Payment</a></li>
        <li><a href="#">Delivery</a></li>
      </ul>
    </div>

    <div>
      <div class="f-title">Connect</div>
      <div class="social">
        <a href="#" class="f-text">Facebook</a>
        <a href="#" class="f-text">Instagram</a>
      </div>
    </div>
  </div>

  <div class="line">
    <div>&copy; Averis</div>
    <div>All rights reserved</div>
  </div>
</footer>