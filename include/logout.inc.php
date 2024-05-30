<?php
function logout(){

    if (!controllaAccesso()) {
        return notificaNOK("Non sei loggato");
    }

    unset($_SESSION['email'], $_SESSION['password']);

    return notificaOK("Ciao ");
}


if (isset($_POST['metodo'])) {
    if ($_POST['metodo'] == 'logout') {
        logout();
        exit;
    }
}

