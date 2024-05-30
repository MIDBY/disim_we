<?php

    class utilityinc extends taglibrary {

        function dummy() {}

        function dudu($name, $id, $pars)
        {
            return "dudu";
        }
        
        function notifylogin($name, $data, $pars) {
            //// funziona

            echo "altro";

            $class = "";
            $msg = "";

            if($data == 0) {
                $msg = "Password o email non corrette!";
                $class = "alert alert-danger";
                break;
            }


            $result ="<div class=\"{$class}\"><button class=\"close\" data-dismiss=\"alert\"></button>{$msg}. </div>";

            return $result;

        }

        function show($name, $data, $pars) {
            global 
                $mysqli;
            
            $main = new Template("skins/revision/dtml/slider.html");

            $oid = $mysqli->query("SELECT * FROM slider");

            if (!$oid) {
                echo "Error {$mysqli->errno}: {$mysqli->error}"; exit;
            } 

            $data = $oid->fetch_all(MYSQLI_ASSOC);

            foreach($data as $slide) {
                
                $template = new Template("skins/revision/dtml/slide_{$slide['type']}.html");
                $template->setContent("title", $slide['title']);
                $template->setContent("subtitle", $slide['subtitle']);
                $main->setContent("item", $template->get());

            }
            
            return $main->get();

        }

        function report($name, $data, $pars) {

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
                    foreach($data as $key => $value) {
                        $report->setContent($key, $value);
                    }
                }

            } while ($data);

            return $report->get();
        }
    }
