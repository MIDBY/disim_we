<?php
require "frame.php";

class p404
{

    private $main;

    private $body;

    public function makeNewTemplates()
    {
        $this->main = makeFrame();

        $this->main->setContent("titolodellapagina", "404");

        makeCss($this->main, "skins/assert_libro/includecss/404.txt");

        $this->body = new Template("skins/html_libro/404.html");
    }


    public function setContentAll()
    {
        $this->body->setContent("linkhome", p404::url() . "listalibri.php");        
        $this->main->setContent("body", $this->body->get());
    }

    public function closemain()
    {
        $this->main->close();
    }

    public static  function url()
    {
        $p = $_SERVER['REQUEST_URI'];
        return substr($p, 0, strpos($p, "404.php"));
    }
}

$m = new p404();

$m->makeNewTemplates();

$m->setContentAll();

$m->closemain();
