<?php


class frameinc extends taglibrary
{

    function dummy()
    {
    }

    function login($name, $idutente, $pars)
    {

        $login = new Template("skins/html_libro/frame/login.html");
        $email = "";

        if (isset($_SESSION['email'])) {
            $email = $_SESSION['email'];
        }
        global $mysqli;
        $oid = $mysqli->query(" SELECT u.nome FROM `utente` u WHERE u.email='{$email}' ; ");
        $data = $oid->fetch_assoc();

        if ($data['nome'] != null) {
            $login->setContent("utente", $data['nome']);
        } else {
            $login->setContent("utente", "Account");
        }

        return $login->get();

        return null;
    }

    function categoria($name, $idutente, $pars)
    {

        $categoria = new Template("skins/html_libro/frame/elementocategoria.html");

        $categoria = frameinc::loadCategoria($categoria);

        return $categoria->get();
    }


    function categoriaexplorer($name, $idutente, $pars)
    {

        $categoria = new Template("skins/html_libro/frame/categoriaexplorer.html");

        $categoria = frameinc::loadCategoria($categoria);

        return $categoria->get();
    }

    function carello($name, $idutente, $pars)
    {
        if (controllaAccesso()) {
            $carello = new Template("skins/html_libro/frame/carello.html");

            $email = $_SESSION['email'];
            global $mysqli;

            $oid = $mysqli->query(" SELECT u.id FROM `utente` u WHERE u.email='{$email}' ; ");

            $data = $oid->fetch_assoc();

            $idutente = $data['id'];

            $carello->setContent("libri", frameinc::libro($mysqli, $idutente));
            $carello->setContent("prezzo_carello", frameinc::prezzo_carello($mysqli, $idutente));

            $carello->setContent("linkdellordine", baseurl() . "conferma.php");

            return $carello->get();
        }
    }

    function menu($name, $idutente, $pars)
    {
        $menu = new Template("skins/html_libro/frame/menu.html");

        $menu->setContent("link", baseurl());

        $categoria = new Template("skins/html_libro/frame/categoria.html");

        $categoria = frameinc::loadCategoria($categoria);

        $menu->setContent("categoria", $categoria->get());

        return $menu->get();
    }


    function linkalto($name, $link, $pars)
    {
        $menu = new Template($link);


        return $menu->get();
    }


    public static function libro($mysqli, $idutente)
    {

        $libro =  new Template("skins/html_libro/frame/libro.html");

        $oid = $mysqli->query("SELECT a.nome, a.cognome, a.id id_autore, l.titolo , l.id id_libro, l.immagine, f.prezzo prezzo , f.id id_formato from `autore_libro_formato` alf join `utente_carello` uc on (uc.idformato = alf.id_formato) join `autore` a on (a.id = alf.id_autore) join `libro` l on (l.id = alf.id_libro) join `formato` f on (f.id = alf.id_formato) WHERE uc.idutente ={$idutente};");
        if (!$oid) {
            // error
        }
        do {
            $data = $oid->fetch_assoc();
            if ($data) {
                foreach ($data as $key => $value) {
                    if ($key == 'immagine') {
                        $img = ridimensionaImmagine($value, 120, 180);
                        $libro->setContent($key, $img);
                    } else {
                        $libro->setContent($key, $value);
                    }
                    if ($key == "id_libro") {
                        $libro->setContent("linklibro", baseurl() . "libro.php?libro=" . $value);
                        $libro->setContent("idlibro", $value);
                    }
                    if ($key == "id_autore") {
                        $libro->setContent("linkautore", baseurl() . "autore.php?autore=" . $value);
                    }
                }
            }
        } while ($data);

        return $libro->get();
    }


    public static function prezzo_carello($mysqli, $idutente)
    {
        $oid = $mysqli->query("SELECT sum(f.prezzo) prezzo_carello from `autore_libro_formato` alf join `utente_carello` uc on (uc.idformato = alf.id_formato) join `autore` a on (a.id = alf.id_autore) join `libro` l on (l.id = alf.id_libro) join `formato` f on (f.id = alf.id_formato) WHERE uc.idutente ={$idutente};");
        if (!$oid) {
            // error
        }
        $data = $oid->fetch_assoc();
        if ($data) {
            foreach ($data as $key => $value) {
                return $value;
            }
        }
    }


    private static function loadCategoria($categoria)
    {
        global $mysqli;
        $oid = $mysqli->query(" SELECT nome FROM `categoria` ; ");
        do {
            $data = $oid->fetch_assoc();
            if ($data) {
                foreach ($data as $key => $value) {
                    $c = substr($value, -strlen($value), 1);
                    $nome = str_replace($c, strtoupper($c), $value);
                    $categoria->setContent("nome", $nome);
                    $categoria->setContent("link", baseurl() . 'listalibri.php?categoria=' . $value);
                }
            }
        } while ($data);
        return $categoria;
    }
}
