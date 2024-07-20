<?php
    session_start();

    require "include/dbms.inc.php";
    require "include/template.inc.php";
    require "include/frameutility.inc.php";
    include "include/tags/admin.inc.php";
    include "include/logout.inc.php";

    if (!isset($_REQUEST['step'])) {
        $_REQUEST['step'] = 0;
    }
/*
    $oid = getUtenteByEmail();
    $data = $oid->fetch_assoc();
*/
    $main = new Template("skins/admin_template/frame-private.html");
    $main->setContent("nome", $data['nome']);
    $main->setContent("ruolo", $data['ruolo']);
    //$main->setContent("immagine", "skins/admin_template/images/image".$data['id_gruppi'].".png");
    $main->setContent("rightmenusidebar", "skins/admin_template/frame-right-menu-sidebar");
    $main->setContent("rightsidebar", "skins/admin_template/frame-right-sidebar");
    $main->setContent("leftsidebar", "skins/admin_template/frame-left-sidebar");
    $main->setContent("home", "skins/admin_template/home");
/*
    if($value = getCountNewOrdine())
        $main->setContent("ordiniaperti", "<span class='badge'>".$value."</span>");
    else
        $main->setContent("ordiniaperti", "");
*/
    if($_REQUEST['step'] == 10) {
            logout();
            header("Refresh: 0");
    }

    require "frame_private_left_sidebar.php";
?>