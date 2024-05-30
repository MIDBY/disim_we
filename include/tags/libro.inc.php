<?php


class libro extends taglibrary
{

    function dummy()
    {
    }


    function infolibro($name, $id, $pars)
    {

        global $mysqli;

        $libro = new Template("skins/html_libro/libro/libro.html");

        $oid = $mysqli->query("SELECT * FROM `libro` l WHERE l.id={$id}");
        if (!$oid) {
            // error
        }

        $data = $oid->fetch_assoc();
        if ($data) {
            foreach ($data as $key => $value) {
                if ($key == 'immagine') {
                    $img = ridimensionaImmagine($value, 300, 452);
                    $libro->setContent($key, $img);
                } else
                    $libro->setContent($key, $value);
            }
        }


        $oid = $mysqli->query("SELECT sum(c.voto) sommadeivoti, count(u.id) numerodicommenti FROM `commenta` c join `utente` u on (c.id_utente = u.id) where c.id_libro = {$id}");
        $data = $oid->fetch_assoc();
        if ($data['sommadeivoti'] != null) {
            $sommadeivoti = $data['sommadeivoti'];
            $numerodicommenti = $data['numerodicommenti'];
            $media = $sommadeivoti / $numerodicommenti;
        }
        else{
            $numerodicommenti = 0;
            $media = 5;
        }

        $libro->setContent("numerodirecensioni", "Numero commenti: ".$numerodicommenti);

        for ($i = 1; $i <= 5; $i++) {
            if ($i <= $media) {
                $libro->setContent("s" . $i, "fas fa-star");
            } else {
                $libro->setContent("s" . $i, "far fa-star");
            }
        }



        $libro->setContent("libriconsigliati", libro::libriconsigliatihtmlf());

        $libro->setContent("carello", libro::carellohtmlf($id));

        $libro->setContent("commentistampahtml", libro::caricaLeRecensioneDellaStampa($id, $mysqli));

        $libro->setContent("autore_html", libro::inserisciLAutore($id, $mysqli));

        $libro->setContent("commenti", libro::commenti($id, $mysqli));

        return $libro->get();
    }


    public static function libriconsigliatihtmlf()
    {

        $libriconsigliati = new Template("skins/html_libro/libro/libriconsigliati.html");
        $linkp = baseurl();
        global $mysqli;

        $oid = $mysqli->query("SELECT a.nome, a.cognome, a.id id_autore, l.titolo, l.immagine, l.id id_libro , f.prezzo FROM `autore` a JOIN scritto s on (s.id_autore= a.id) JOIN libro l on (s.id_libro = l.id) JOIN libro_formato lf on (lf.id_libro = l.id) join formato f on (f.id = lf.id_formato) where l.visibile=1 ORDER BY RAND() Limit 4 ");
        do {
            $data = $oid->fetch_assoc();
            if ($data) {
                foreach ($data as $key => $value) {
                    if ($key == 'immagine') {
                        $img = ridimensionaImmagine($value, 120, 180);
                        $libriconsigliati->setContent($key, $img);
                    } else {
                        $libriconsigliati->setContent($key, $value);
                    }
                }
                foreach ($data as $key => $value) {
                    if ($key == 'id_libro') {
                        $libriconsigliati->setContent("link", $linkp . "libro.php?libro=" . $value);
                    }
                    if ($key == 'id_autore') {
                        $libriconsigliati->setContent("autore", $linkp . "autore.php?autore=" . $value);
                    }
                }
            }
        } while ($data);

        return $libriconsigliati->get();
    }


    public static function commenti($id, $mysqli)
    {

        $commento = new Template("skins/html_libro/libro/commento.html");

        $oid = $mysqli->query("SELECT sum(c.voto) sommadeivoti, count(u.id) numerodicommenti FROM `commenta` c join `utente` u on (c.id_utente = u.id) where c.id_libro = {$id}");
        $data = $oid->fetch_assoc();
        if ($data['sommadeivoti'] != null) {
            $sommadeivoti = $data['sommadeivoti'];
            $numerodicommenti = $data['numerodicommenti'];
            $media = $sommadeivoti / $numerodicommenti;
            $mediaV = substr($media, 0, strrpos($media, ".") + 2);
            $commento->setContent("mediavoti", $mediaV);
            $commento->setContent("numerodirecensioni", $numerodicommenti);

            for ($i = 1; $i <= 5; $i++) {
                if ($i <= $media) {
                    $commento->setContent("s" . $i, "fas fa-star");
                } else {
                    $commento->setContent("s" . $i, "far fa-star");
                }
            }
        }

        $commento->setContent("commentosingolo", libro::caricaCommenti($id));

        $scrivicommento = new Template("skins/html_libro/libro/scrivicommento.html");

        $scrivicommento->setContent("id_libro", $id);

        $commento->setContent("scrivicommento", $scrivicommento->get());

        return $commento->get();
    }


    public static function caricaCommenti($id)
    {

        global $mysqli;

        $commentosingolo = new Template("skins/html_libro/libro/commentosingolo.html");

        $oid = $mysqli->query("SELECT c.data, c.titolo, c.voto , c.commento, u.nome FROM `commenta` c join `utente` u on (c.id_utente = u.id) where c.id_libro = {$id}");
        if (!$oid) {
            // error
        }

        do {
            $data = $oid->fetch_assoc();
            if ($data) {
                foreach ($data as $key => $value) {
                    if ($key != 'voto') {
                        $commentosingolo->setContent($key, $value);
                    } else {
                        for ($i = 1; $i <= 5; $i++) {
                            if ($i <= $value) {
                                $commentosingolo->setContent("s" . $i, "fas fa-star");
                            } else {
                                $commentosingolo->setContent("s" . $i, "far fa-star");
                            }
                        }
                    }
                }
            }
        } while ($data);


        return $commentosingolo->get();
    }


    public static function carellohtmlf($id)
    {

        global $mysqli;

        $carello = new Template("skins/html_libro/libro/carello.html");

        $formato = new Template("skins/html_libro/libro/formato.html");


        $oid = $mysqli->query("SELECT f.id iddelfromato , f.nome, f.prezzo prezzodelformatoselezionato FROM libro_formato lf join formato f on (f.id = lf.id_formato) WHERE lf.id_libro={$id} and f.visibile=1");
        if (!$oid) {
            // error
        }

        do {
            $data = $oid->fetch_assoc();
            if ($data) {
                foreach ($data as $key => $value) {
                    $formato->setContent($key, $value);
                }
            }
        } while ($data);


        $carello->setContent("formato", $formato->get());

        return $carello->get();
    }

    public static function caricaLeRecensioneDellaStampa($id, $mysqli)
    {
        $commentistampa = new Template("skins/html_libro/libro/commentistampa.html");

        $oid = $mysqli->query("SELECT * FROM `recensisce` r join `testa_giornalistica` t on(r.id_testa_giornalistica = t.id)  WHERE r.id_libro ={$id}");

        if (!$oid) {
            // error
        }

        do {
            $data = $oid->fetch_assoc();
            if ($data) {
                foreach ($data as $key => $value) {
                    $commentistampa->setContent($key, $value);
                }
            }
        } while ($data);

        return $commentistampa->get();
    }


    public static function inserisciLAutore($id, $mysqli)
    {

        $main = new Template("skins/html_libro/libro/autore.html");

        $oid = $mysqli->query("SELECT nome nomeautorel, cognome cognomeautorel , a.id  FROM `libro` l JOIN `scritto` s on (l.id = s.id_libro) JOIN `autore` a on (s.id_autore = a.id)  WHERE s.id_libro ={$id}");

        if (!$oid) {
            // error
        }


        $baselink = baseurl();

        $data = $oid->fetch_assoc();
        if ($data) {
            foreach ($data as $key => $value) {
                $main->setContent($key, $value);
            }
            foreach ($data as $key => $value) {
                if ($key == 'id') {
                    $main->setContent("linkautore", $baselink . "autore.php?autore=" . $value);
                }
            }
        }
        return $main->get();
    }
}
