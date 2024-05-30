<?php
    require "frame_private.php";
    include "include/account.inc.php";

    controllaAccessoEIPermessi("admin_home.php");

    if (!isset($_REQUEST['step'])) {
        $_REQUEST['step'] = 0;
    }

    $form = new Template("skins/pluto/dtml/settings.html");

    $oid = getUtenteByEmail();
    if(!$oid){
        //error
    } else {
        $data = $oid->fetch_assoc();

        //riempio la pagina con i dati dell'utente
        $form->setContent("immagine", "skins/pluto/images/logo/image".$data['id_gruppi'].".png");
        $form->setContent("nome", $data['nome']);
        $form->setContent("cognome", $data['cognome']);
        $form->setContent("email", $data['email']);
        $form->setContent("indirizzo", $data['indirizzo']);
        $form->setContent("password", $_SESSION['password']);

    }
    
    switch ($_REQUEST['step']) { 
        case 0: // form emission
            
            break;

        case 1: // edit transaction
            if(isset($_REQUEST['aggiornaaccount'])) {
                aggiornaaccount();
            }
            
            header("Refresh: 0");
            break;
        
        case 2:

            if(isset($_POST['modificapassword'])) {
                modificapassword();
            }

            header("Refresh: 0");
            break;
    }


    $main->setContent("body", $form->get());
    $main->close();

?>