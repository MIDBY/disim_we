<?php
    require "frame_private.php";
    //include "skins/pluto/data/data_months_sells.php";

    //controllaAccessoEIPermessi('admin_home.php');

    $body = new Template("skins/admin_template/home.html");
/*
    //inserisco i dati nel label utenti iscritti
    $oid = getCountUtentiGruppi();
    $data = $oid->fetch_assoc();
    $body->setContent("numeroutenti", $data['count']);

    //inserisco i dati nel label fatturato mensile 
    $oid = getFatturatoMensile();
    $data = $oid->fetch_assoc();
    $body->setContent("fatturatomensile", $data['sommaprezzi']." €");

    //inserisco i dati nel label documenti e lo rendo cliccabile
    if(controllaPermessi('format_edit.php'))
        $body->setContent("linkformati", 'format_edit.php');
    else
        $body->setContent("linkformati", "");
    $oid = getCountDocumentiFormato();
    $data = $oid->fetch_assoc();
    $body->setContent("numerodocumenti", $data['count']);

    //inserisco i dati nel label commenti e lo rendo cliccabile
    if(controllaPermessi('comment_delete.php'))
        $body->setContent("linkcommenti", 'comment_delete.php');
    else
        $body->setContent("linkcommenti", "");
    $oid = getCountCommenta();
    $data = $oid->fetch_assoc();
    $body->setContent("numerocommenti", $data['count']);

    //Carosello
    $oid = getUtentiVip();
    $first_val = true;

    while($data = $oid->fetch_assoc()) {    
        $carousel = new Template("skins/pluto/dtml/form_carousel.html");
        if($first_val){
            $carousel->setContent("attivo", "active");
            $first_val = false;
        } else {
            $carousel->setContent("attivo", "");
        }
        $carousel->setContent("immagine", "skins/pluto/images/logo/image".$data['id_gruppi'].".png");
        $carousel->setContent("nome", $data['nome']);
        $carousel->setContent("cognome",$data['cognome']);
        $carousel->setContent("gruppo", $data['nomegruppo']);

        $body->setContent("carosello", $carousel->get());
    }

*/
    $main->setContent("body", $body->get());
    $main->close();


?>