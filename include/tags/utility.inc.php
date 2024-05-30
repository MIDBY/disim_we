<?php

class utility extends taglibrary
{

    function dummy()
    {
    }

    function dudu($name, $id, $pars)
    {
        return "dudu";
    }

    function notificanok($name, $msg, $pars)
    {
        $class = "alert alert-danger";
        return utility::notifica($class, $msg);
    }

    function notificaok($name, $msg, $pars)
    {
        $class = "alert alert-success";
        return utility::notifica($class, $msg);
    }


    public static function notifica($class, $msg)
    {
        return  "<div class=\"{$class}\"><button class=\"close\" data-dismiss=\"alert\"></button>{$msg}. </div>";
    }

    function show($name, $data, $pars)
    {
        global
            $mysqli;

        $main = new Template("skins/revision/dtml/slider.html");

        $oid = $mysqli->query("SELECT * FROM slider");

        if (!$oid) {
            echo "Error {$mysqli->errno}: {$mysqli->error}";
            exit;
        }

        $data = $oid->fetch_all(MYSQLI_ASSOC);

        foreach ($data as $slide) {

            $template = new Template("skins/revision/dtml/slide_{$slide['type']}.html");
            $template->setContent("title", $slide['title']);
            $template->setContent("subtitle", $slide['subtitle']);
            $main->setContent("item", $template->get());
        }

        return $main->get();
    }

    function report($name, $data, $pars)
    {

        global $mysqli;

        $report = new Template("skins/webarch/dtml/report.html");
        $report->setContent("name", $pars['name']);

        $oid = $mysqli->query("SELECT {$pars['fields']} FROM {$pars['table']}");
        if (!$oid) {
            // error
        }
        do {
            $data = $oid->fetch_assoc();
            if ($data) {
                foreach ($data as $key => $value) {
                    $report->setContent($key, $value);
                }
            }
        } while ($data);

        return $report->get();
    }
}
