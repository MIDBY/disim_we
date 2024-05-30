<?php
session_start();
require "include/template.inc.php";
require "include/dbms.inc.php";
require "include/frameutility.inc.php";
require "include/signin.inc.php";
require "include/login.inc.php";
require "include/logout.inc.php";

function makeFrame()
{
    $mf = new Template("skins/html_libro/frame/frame.html");

    $barrainalto = new Template("skins/html_libro/frame/barrainalto.html");

    $newsletter = new Template("skins/html_libro/frame/newsletter.html");

    $explorer = new Template("skins/html_libro/frame/explorer.html");

    $explorer -> setContent("linkabout", baseurl() . "altro.php?pagina=about" );

    $explorer -> setContent("linkmappa", baseurl() . "altro.php?pagina=contatti" );

    $explorer -> setContent("linkcondizioni", baseurl() . "altro.php?pagina=condizioni" );

    $barrasocietaassociate = new Template("skins/html_libro/frame/barrasocietaassociate.html");

    $framejs = new Template("skins/assert_libro/includejavascript/frame.html"); 

    mostraErrore($mf);

    $framejs->setContent(baseurl()."link_admin", $barrainalto->get());

    $mf->setContent("barrainalto", $barrainalto->get());

    $mf->setContent("newsletter", $newsletter->get());

    $mf->setContent("explorer", $explorer->get());

    $mf->setContent("importjavascriptframe", $framejs->get());

    $mf->setContent("barrasocietaassociate", $barrasocietaassociate->get());

    return $mf;
}

function mostraErrore($mf)
{
    if (isset($_GET['messaggio'])) {
        $messaggio = $_GET['messaggio'];
        $t = new Template("skins/html_libro/utility/notificanok.html");
        $t->setContent("ph", $messaggio);
        $mf->setContent("messaggiodierrore", $t->get());
    }
}


function makeCss($mf, $path)
{
    $importcss = new Template($path);
    $mf->setContent("importcss", $importcss->get());
}


function makeJavaScript($mf, $path)
{
    $importjs = new Template($path);
    $mf->setContent("importjavascript", $importjs->get());
}


function baseurl()
{
    $p = $_SERVER['REQUEST_URI'];
    $link = $_SERVER['PHP_SELF'];
    $link_array = explode('/', $link);
    $page = end($link_array);
    return substr($p, 0, strpos($p, $page));
}