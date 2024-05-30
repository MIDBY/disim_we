<?php

function login()
{

    if (!controllaAccessologin()) {
        return notificaNOK("Error");
    }

    $email = $_POST['email'];
    $pass = $_POST['pass'];

    $_SESSION['email'] = $email;
    $_SESSION['password'] = $pass;

    if (controllaLevelUp()) {
        echo "admin";
        exit();
    }

    return notificaOK("Ciao " . $email);
}


function controllaAccessologin()
{

    $email = $_POST['email'];
    $pass = $_POST['pass'];

    $c = crypto($pass);

    global $mysqli;

    $oid = $mysqli->query(" SELECT * FROM `utente` u WHERE u.email='{$email}' and u.password='{$c}'; ");

    $data = $oid->fetch_assoc();

    if ($oid) {
        if ($data['email'] == null || $data['password'] == null) {
            return false;
        }
    }
    return true;
}

function controllaLevelUp()
{
    global $mysqli;
    $email = $_SESSION['email'];
    $c = crypto($_SESSION['password']);

    $oid = $mysqli->query(" SELECT utenti_gruppi.id_gruppi id_gruppo FROM utenti_gruppi join utente on utente.id = utenti_gruppi.id_utente WHERE email = '{$email}' and password='{$c}'; ");
    $data = $oid->fetch_assoc();

    if ($data['id_gruppo'] > 1) {
        return true;
    }
    return false;
}

if (isset($_POST['metodo'])) {
    if ($_POST['metodo'] == 'login') {
        login();
        exit;
    }
}
