<?php
//require "frame.php";
require "include/template.inc.php";

if (!isset($_POST['utente']) || !isset($_POST['password'])) {
    $body = new Template("skins/html_libro/notifica.html");

    $body->setContent("placeholder", 0);

    $body-> close();
}

$body = new Template("skins/html_libro/notifica.html");

$body->setContent("notifylogin", 0);

return $body->get();
