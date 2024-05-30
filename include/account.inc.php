<?php

function aggiornaemail()
{

    if (!controllaAccesso()) {
        return notificaNOK("Error");
    }

    global $mysqli;
    $email = $_SESSION['email'];

    $oid = $mysqli->query("SELECT id FROM utente WHERE `email` = '{$email}';");
    $data = $oid->fetch_assoc();
    $id = $data['id'];

    $nuovaemail = $_POST['email'];
    
    if(isset($id) && $id > 0)
        $mysqli->query("UPDATE `utente` SET `email` = \"{$nuovaemail}\" WHERE `utente`.`id` = '{$id}';");

    return notificaOK("Ciao");
}

function aggiornaaccount()
{

    if (!controllaAccesso()) {
        return notificaNOK("Error");
    }

    global $mysqli;

    $email = $_SESSION['email'];

    $nome = $_POST['nome'];
    $cognome = $_POST['cognome'];
    $indirizzo = $_POST['indirizzo'];

    $mysqli->query("UPDATE `utente` SET `indirizzo` = \"{$indirizzo}\" , `nome` =  '{$nome}' , `cognome` = '{$cognome}' WHERE `utente`.`email` = '{$email}';");

    if($_POST['email'] !== $email) {
        aggiornaemail();
    }

    return notificaOK("Ciao");
}

function modificapassword()
{

    if (!controllaAccesso()) {
        return notificaNOK("Error");
    }

    $vecchiapassword = $_POST['vecchiapassword'];
    $email = $_SESSION['email'];
    $nuovapassword1 = $_POST['nuovapassword1'];
    $nuovapassword2 = $_POST['nuovapassword2'];

    $c = crypto($vecchiapassword);

    global $mysqli;

    $oid = $mysqli->query(" SELECT * FROM `utente` u WHERE u.email=\"{$email}\" and u.password='{$c}'; ");

    $data = $oid->fetch_assoc();

    if (isset($data['email']) AND $data['email']  == "") {
        return notificaNOK("Vecchia password non corretta");
    }


    if ($nuovapassword1 != $nuovapassword2) {
        return notificaNOK("Le password non sono uguali");
    }

    $c = crypto($nuovapassword1);

    $mysqli->query("UPDATE `utente` SET `password` = '{$c}' WHERE `utente`.`email` = \"{$email}\";");

    $_SESSION['password'] = $_POST['nuovapassword1'];

    return notificaOK("Password aggiornata");
}



if (isset($_POST['metodo'])) {
    if ($_POST['metodo'] == 'aggiornaaccount') {
        aggiornaaccount();
        exit;
    }
    if ($_POST['metodo'] == 'modificapassword') {
        modificapassword();
        exit;
    }
}
