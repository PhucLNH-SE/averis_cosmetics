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
        grid-template-columns: minmax(280px, 1.25fr) repeat(3, minmax(160px, 1fr));
        gap:32px;
        align-items:start;
    }

    .footer-col{
        min-width:0;
    }

    .footer-brand{
        padding-right:18px;
    }

    .f-title{
        font-weight:700;
        text-transform:uppercase;
        margin-bottom:14px;
        font-size:14px;
        letter-spacing:.8px;
        color:#111;            /* title color */
    }

    .f-text{
        color:#111;            /* content color */
        font-size:13px;
        line-height:1.85;
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

    .footer ul{
        list-style:none;
        padding:0;
        margin:0;
    }
    .footer li{
        margin:8px 0;
    }

    .social ul{
        display:flex;
        flex-direction:column;
        gap:8px;
    }

    .line{
        width:92%;
        max-width:1200px;
        margin:28px auto 0;
        border-top:1px solid rgba(0,0,0,.12);  /* light line */
        padding-top:14px;
        color:rgba(0,0,0,.65);                 /* line text dark slightly */
        font-size:12px;
        display:flex;
        justify-content:space-between;
        align-items:center;
        flex-wrap:wrap;
        gap:10px;
    }

    @media(max-width:900px){
        .footer .wrap{
            grid-template-columns:1fr 1fr;
        }
    }
    @media(max-width:520px){
        .footer .wrap{
            grid-template-columns:1fr;
        }
    }
</style>
<footer class="footer">
    <div class="wrap">
        <div class="footer-col footer-brand">
            <div class="f-title">AVERIS COSMETICS</div>
            <div class="f-text">
                Averis Cosmetics Business<br>
                Hotline: 0966434787<br>
                Email: support@averis.com
            </div>
        </div>

        <div class="footer-col">
            <div class="f-title">Support</div>
            <ul>
                <li><a href="#">Privacy Policy</a></li>
                <li><a href="#">Shipping</a></li>
                <li><a href="#">Returns</a></li>
            </ul>
        </div>

        <div class="footer-col">
            <div class="f-title">Guides</div>
            <ul>
                <li><a href="#">How to Buy</a></li>
                <li><a href="#">Payment</a></li>
                <li><a href="#">Delivery</a></li>
            </ul>
        </div>

        <div class="footer-col">
            <div class="f-title">Connect</div>
            <div class="social">
                <ul>
                    <li> <a href="#" class="f-text">Facebook</a> </li>
                    <li><a href="#" class="f-text">Instagram</a></li>
                </ul>
            </div>
        </div>
    </div>

    <div class="line">
        <div>&copy; Averis</div>
        <div>All rights reserved</div>
    </div>
</footer>

<jsp:include page="/views/common/popup.jsp" />
