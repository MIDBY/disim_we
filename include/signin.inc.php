<?php


function signin()
{
    $email = $_POST['email'];
    $nome = $_POST['nome'];
    $cognome = $_POST['cognome'];
    $pass1 = $_POST['pass1'];
    $pass2 = $_POST['pass2'];

    if ($pass1 != $pass2) {
        return notificaNOK("Le password hanno un valore diverso");
    }

    global $mysqli;

    $c = crypto($pass1);

    $oid = $mysqli->query(" call inserisci_utente ( '{$email}' , '{$c}' , '{$nome}' , '{$cognome}' );");

    if (!$oid) {
        return notificaNOK("Error");
    }

    $_SESSION['email'] = $email;
    $_SESSION['password'] = $pass1;

    return notificaOK("Ciao " . $email);
}

if (isset($_POST['metodo'])) {
    if ($_POST['metodo'] == 'signin') {
        signin();
        exit;
    }
}
