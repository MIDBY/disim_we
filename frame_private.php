<?php
    session_start();

    require "include/dbms.inc.php";
    require "include/template.inc.php";
    require "include/frameutility.inc.php";
    include "include/tags/admin.inc.php";
    include "include/logout.inc.php";
    require "admin_sidebar.php";

    if (!isset($_REQUEST['step'])) {
        $_REQUEST['step'] = 0;
    }

    $oid = getUtenteByEmail();
    $data = $oid->fetch_assoc();

    $main = new Template("skins/admin_template/index.html");
    $main->setContent("nome", $data['nome']);
    $main->setContent("cognome", $data['cognome']);
    $main->setContent("immagine", "skins/pluto/images/logo/image".$data['id_gruppi'].".png");

    if($value = getCountNewOrdine())
        $main->setContent("ordiniaperti", "<span class='badge'>".$value."</span>");
    else
        $main->setContent("ordiniaperti", "");

    if($_REQUEST['step'] == 10) {
            logout();
            header("Refresh: 0");
    }
?>