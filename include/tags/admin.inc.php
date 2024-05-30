<?php

//-----------------Appartiene----------------------------------

function setAppartiene($id_libro, $id_categoria)
{
    global $mysqli;
    $oid = $mysqli->query("SELECT count(*) as count FROM appartiene WHERE id_libro = " . $id_libro . ";");
    $data = $oid->fetch_assoc();
    //Se 0 non esiste la relazione tra libro e categoria, altrimenti esiste e bisogna aggiornarla
    if ($data['count'] == 0)
        $mysqli->query("INSERT INTO appartiene (id_libro, id_categoria) VALUES (" . $id_libro . ", " . $id_categoria . ");");
    else
        $mysqli->query("UPDATE appartiene SET id_libro = " . $id_libro . ", id_categoria = " . $id_categoria . " WHERE id_libro = " . $id_libro . ";");
}

//-----------------Autore----------------------------------

function getAutore()
{
    global $mysqli;
    return $mysqli->query("SELECT autore.id, autore.nome, autore.cognome, autore.presentazione, autore.immagine_piccola, autore.immagine_grande
                FROM autore order by autore.cognome asc;");
}

function getAutoreByID($id_autore)
{
    global $mysqli;
    return $mysqli->query("SELECT nome, cognome, presentazione, immagine_piccola, immagine_grande FROM autore 
        WHERE id = " . $id_autore . " order by id asc;");
}

function getCountLibriAutore($id_autore)
{
    global $mysqli;
    return $mysqli->query("SELECT COUNT(id_autore) as count FROM scritto join libro on scritto.id_libro = libro.id
                            WHERE visibile = 1 AND id_autore = " . $id_autore . ";");
}

function uploadImgAutore($image_small, $image_big)
{
    eliminaImmagineAutore($image_big, $image_small);
    $pathimmaginepiccola = caricaImmagineNelfileSystem('immagine_piccola', $image_small, $image_big);
    $pathimmaginegrande = caricaImmagineNelfileSystem('immagine_grande', $image_small, $image_big);

    return array($pathimmaginepiccola, $pathimmaginegrande);
}

function caricaImmagineNelfileSystem($immagine, $image_small, $image_big)
{
    global $mysqli;
    if ($_FILES[$immagine]["name"] != "") {
        $_SESSION['sourceImage'] = $immagine;
        require "upload_image.php";
        $path = $_SESSION['destImage'];
        if ($_SESSION['message'] != null)
            throw new Exception($_SESSION['message']);
    } else {
        $oid = $mysqli->query("SELECT * from autore where immagine_piccola =  '{$image_small}' or immagine_grande = '{$image_big}' ; ");
        $data = $oid->fetch_assoc();
        $path = $data[$immagine];
        echo "qui";
    }
    return $path;
}

function eliminaImmagineAutore($image_big, $image_small)
{
    global $mysqli;
    if (isset($image_small) and  isset($image_big)) {
        $oid = $mysqli->query("SELECT * from autore where 
                        immagine_piccola =  '{$image_small}' or immagine_grande = '{$image_big}' ; ");
        $data = $oid->fetch_assoc();
        if ($data['conta'] > 0 && $_FILES['immagine_grande']["name"] != "")
            unlink($image_small);
        if ($data['conta'] > 0 && $_FILES['immagine_piccola']["name"] != "")
            unlink($image_big);
    }
}

function setAutore($id_autore, $nome, $cognome, $presentazione, $image_small, $image_big)
{
    global $mysqli;
    if (!isset($id_autore) or $id_autore == 0) {
        //sto creando un nuovo autore
        $mysqli->query("INSERT INTO autore (nome, cognome, presentazione, immagine_piccola, immagine_grande) VALUES 
                            (\"" . $nome . "\", \"" . $cognome . "\", \"" . addslashes($presentazione) . "\", '" . $image_small . "', '" . $image_big . "');");

        $_SESSION['autore'] = mysqli_insert_id($mysqli);
    } else {
        $mysqli->query("UPDATE autore SET nome = '" . $nome . "', cognome = \"" . $cognome . "\",
                                    presentazione = \"" . addslashes($presentazione) . "\", immagine_piccola = '" . $image_small . "',
                                    immagine_grande = '" . $image_big . "' WHERE id = " . $id_autore . ";");
    }
}

function deleteAutore($id_autore)
{
    global $mysqli;
    //elimino le immagini che non sono usate in altre istanze
    //recupero le due immagini dell'autore selezionato (se ci sono) e se non hanno occorrenze le elimino dalla cartella per inutilizzo
    $oid = $mysqli->query("SELECT immagine_piccola, immagine_grande FROM autore WHERE id = " . $id_autore . ";");
    $data = $oid->fetch_assoc();
    if ($data['immagine_piccola']) {
        $oid2 = $mysqli->query("SELECT count(*)-1 as count from autore where 
                immagine_piccola = '" . $data['immagine_piccola'] . "' or immagine_grande = '" . $data['immagine_piccola'] . "';");
        $data2 = $oid2->fetch_assoc();
        if ($data2['count'] == 0)
            unlink($data['immagine_piccola']);
    }
    if ($data['immagine_piccola']) {
        $oid2 = $mysqli->query("SELECT count(*)-1 as count from autore where 
                immagine_piccola = '" . $data['immagine_piccola'] . "' or immagine_grande = '" . $data['immagine_piccola'] . "';");
        $data2 = $oid2->fetch_assoc();
        if ($data2['count'] == 0)
            unlink($data['immagine_piccola']);
    }

    //ora procedo all'eliminazione formale dell'autore        
    $mysqli->query("DELETE from autore where id = " . $id_autore . ";");
}

//-----------------Categoria----------------------------------

function getCategoria()
{
    global $mysqli;
    return $mysqli->query("SELECT id, nome FROM categoria order by nome asc;");
}

function getCategoriaByID($id_categoria)
{
    global $mysqli;
    return $mysqli->query("SELECT nome FROM categoria 
                                WHERE id = " . $id_categoria . ";");
}

function getCountLibriCategoria($id_categoria)
{
    global $mysqli;
    return $mysqli->query("SELECT COUNT(id_categoria) as count FROM appartiene
                            WHERE id_categoria = " . $id_categoria . ";");
}

function setCategoria($id_categoria, $nome)
{
    global $mysqli;
    if ($id_categoria == 0) {
        //sto creando una nuova categoria
        $oid = $mysqli->query("INSERT INTO categoria (nome) VALUES 
                            (\"" . addslashes($nome) . "\");");
    } else {
        $oid = $mysqli->query("UPDATE categoria SET nome = \"" . addslashes($nome) . "\" WHERE id = " . $id_categoria . ";");

        if (!$oid) {
            //error
        }
    }
}

function deleteCategoria($id_categoria)
{
    global $mysqli;
    $mysqli->query("DELETE from categoria where id = " . $id_categoria . ";");
}

//-----------------Commenta----------------------------------

function getCommenta()
{
    global $mysqli;
    return $mysqli->query("SELECT commenta.titolo, commenta.data, commenta.voto, commenta.commento, commenta.id_utente, commenta.id_libro, utente.nome, utente.cognome, libro.titolo as libro 
						from commenta left join utente on commenta.id_utente = utente.id left join libro on commenta.id_libro = libro.id order by commenta.id_libro asc;");
}

function getCountCommenta()
{
    global $mysqli;
    return $mysqli->query("SELECT count(*) as count FROM commenta");
}

function deleteCommenta($id_utente, $id_libro, $data)
{
    global $mysqli;
    $mysqli->query("DELETE from commenta where id_libro = " . $id_libro . " AND id_utente = " . $id_utente . " AND data = '" . $data . "';");
}

//-----------------Formato----------------------------------
function getFormato()
{
    global $mysqli;
    return $mysqli->query("SELECT formato.id, formato.codice, libro.titolo, formato.nome, formato.prezzo, formato.link
                 from formato left join libro_formato on formato.id = libro_formato.id_formato 
                 left join libro on libro.id = libro_formato.id_libro WHERE formato.visibile = 1 order by libro.titolo asc;");
}

function getFormatoByID($id_formato)
{
    global $mysqli;
    return $mysqli->query("SELECT formato.codice, formato.nome, formato.prezzo, formato.link, libro_formato.id_libro
                        from formato left join libro_formato on id_formato = formato.id where formato.id = " . $id_formato . ";");
}

function getFormatoByLibro($id_libro)
{
    global $mysqli;
    return $mysqli->query("SELECT formato.id, formato.codice, formato.nome, formato.prezzo, formato.link 
                    from formato join libro_formato on libro_formato.id_formato = formato.id
                    WHERE formato.visibile = 1 and libro_formato.id_libro = " . $id_libro . ";");
}

function getCountDocumentiFormato()
{
    global $mysqli;
    return $mysqli->query("SELECT DISTINCT count(*) as count FROM formato; ");
}

function getCountFormato($id_formato)
{
    global $mysqli;
    $oid = $mysqli->query("SELECT COUNT(*) as count FROM composto WHERE id_formato = " . $id_formato . ";");
    $data = $oid->fetch_assoc();
    $sum_util_formato = $data['count'];

    $oid = $mysqli->query("SELECT COUNT(*) as count FROM contiene WHERE id_formato = " . $id_formato . ";");
    $data = $oid->fetch_assoc();
    $sum_util_formato += $data['count'];

    return $sum_util_formato;
}

function uploadFileFormato($new_link)
{
    //vado a caricare il file
    if ($_FILES['link']['name'] != "") {
        $_SESSION['sourceFile'] = "link";
        require "upload_file.php";
        $message = $_SESSION['message'];
        if (isset($_SESSION['destFile'])) {
            //cancello il file precedente se c'é e non é stato utilizzato da altre parti
            if ($new_link != "") {
                $oid = $mysqli->query("SELECT count(*) -1 as count from formato where 
                                link = '" . $new_link . "'; ");
                $data2 = $oid->fetch_assoc();
                if ($data2['count'] == 0)
                    unlink($new_link);
            }
            $new_link = $_SESSION['destFile'];
        }
    }
    return array($message, $new_link);
}

function setFormato($id_formato, $codice, $nome, $prezzo, $new_link)
{
    global $mysqli;
    if (!isset($id_formato) or $id_formato == 0) {
        //sto creando un nuovo formato

        $mysqli->query("INSERT INTO formato (codice, nome, prezzo, link) VALUES ('$codice', '$nome', '$prezzo', '$new_link');");

        $_SESSION['formato'] = mysqli_insert_id($mysqli);
    } else {
        $mysqli->query("UPDATE formato SET codice = \"" . $codice . "\", nome = \"" . addslashes($nome) . "\", 
                    prezzo = \"" . $prezzo . "\", link = \"" . $new_link . "\" WHERE id = " . $id_formato . ";");
    }
}

function deleteFormato($id_formato)
{
    global $mysqli;
    if (getCountFormato($id_formato) == 0) {
        $oid = $mysqli->query("SELECT link FROM formato WHERE id = " . $id_formato . ";");
        $data = $oid->fetch_assoc();

        //lascio il file se é usato in altre istanze
        //(se c'é) elimino il file se non ha occorrenze dalla cartella per inutilizzo
        if (isset($data['link']) and $data['link'] != "") {
            $oid2 = $mysqli->query("SELECT count(*)-1 as count from formato WHERE link = '" . $data['link'] . "';");
            $data2 = $oid2->fetch_assoc();
            if ($data2['count'] == 0)
                unlink($data['link']);
        }

        //ora procedo all'eliminazione formale del formato
        $mysqli->query("DELETE from wishlist where id_formato = " . $id_formato . ";");
        $mysqli->query("DELETE from libro_formato where id_formato = " . $id_formato . ";");
        $mysqli->query("DELETE from formato where id = " . $id_formato . ";");
    } else {
        //lo rendo invisibile perché viene utilizzato
        $mysqli->query("UPDATE formato set visibile = 0 where id = " . $id_formato . ";");
    }
}

//-----------------Gruppo----------------------------------

function getGruppo()
{
    global $mysqli;
    return $mysqli->query("SELECT id, nome FROM gruppo");
}

//-----------------Libro_Formato----------------------------------
function setLibroFormato($id_libro, $id_formato, $old_id_libro)
{
    global $mysqli;
    //se il libro selezionato è 0, significa che devo creare la connessione
    if ($old_id_libro == 0) {
        $mysqli->query("INSERT into libro_formato (id_libro, id_formato) VALUES ('" . $id_libro . "', '" . $id_formato . "');");
    } else {
        //se è stato inserito un libro in $old_id_libro allora già esiste la connessione e la vado a sovrascrivere
        $mysqli->query("UPDATE libro_formato SET id_libro = " . $id_libro . " WHERE id_formato = " . $id_formato . ";");
    }
}

//-----------------Libro----------------------------------

function getLibro()
{
    global $mysqli;
    return $mysqli->query("SELECT id, titolo FROM libro where visibile = 1 order by titolo asc;");
}

function getLibroByID($id_libro)
{
    global $mysqli;
    return $mysqli->query("SELECT libro.id, libro.titolo, libro.descrizione, libro.immagine, libro.specifiche,
                            scritto.id_autore, appartiene.id_categoria
                            FROM libro left join scritto ON scritto.id_libro = libro.id 
                            left join appartiene on appartiene.id_libro = libro.id 
                            WHERE libro.id = " . $id_libro . " order by libro.id asc;");
}

function uploadImgLibro($image)
{
    unset($oid, $message);

    if ($_FILES['immagine']['name'] != "") {
        $_SESSION['sourceImage'] = "immagine";
        require "upload_image.php";
        $message = $_SESSION['message'];
        if (isset($_SESSION['destImage'])) {

            //cancello immagine precedente se c'é e non é stata utilizzata da altre parti
            if (isset($image)) {
                $oid = $mysqli->query("SELECT count(*) -1 as count from libro where 
                                immagine = '" . $image . "'; ");
                $data = $oid->fetch_assoc();
                if ($data['count'] == 0)
                    unlink($image);
            }

            $image = $_SESSION['destImage'];
        }
    }
    return array($message, $image);
}

function setLibro($id_libro, $titolo, $descrizione, $specifiche, $image)
{
    global $mysqli;
    if (!isset($id_libro) or $id_libro == 0) {
        //sto creando un nuovo libro
        $mysqli->query("INSERT INTO libro (titolo, descrizione, specifiche, immagine) VALUES 
                            ('" . $titolo . "', \"" . addslashes($descrizione) . "\",
                            \"" . addslashes($specifiche) . "\", '" . $image . "' );");

        $_SESSION['libro'] = mysqli_insert_id($mysqli);
    } else {
        $mysqli->query("UPDATE libro SET titolo = '" . addslashes($titolo) . "', descrizione = \"" . addslashes($descrizione) . "\",
                                    specifiche = \"" . addslashes($specifiche) . "\", immagine = '" . $image . "'
                                    WHERE id = " . $id_libro . ";");
    }
}

function deleteLibro($id_libro)
{
    global $mysqli;
    $sum_used = 0;

    $oid = $mysqli->query("SELECT id_formato FROM libro_formato where id_libro = " . $id_libro . ";");

    while ($data = $oid->fetch_assoc()) {
        $sum_used += getCountFormato($data['id_formato']);
    }

    if ($sum_used == 0) {
        //elimino tutto

        //lascio il file se é usato in altre istanze
        //(se c'é) elimino il file se non ha occorrenze dalla cartella per inutilizzo
        $oid = $mysqli->query("SELECT immagine from libro WHERE id = " . $id_libro . ";");
        if (isset($data['immagine']) and $data['immagine'] != "") {
            $oid2 = $mysqli->query("SELECT count(*)-1 as count from libro WHERE immagine = '" . $data['immagine'] . "';");
            $data2 = $oid2->fetch_assoc();
            if ($data2['count'] == 0)
                unlink($data['immagine']);
        }

        //ora procedo all'eliminazione formale delle varie parti di libro
        $mysqli->query("DELETE from appartiene where id_libro = " . $id_libro . ";");
        $mysqli->query("DELETE from commenta where id_libro = " . $id_libro . ";");

        $oid = $mysqli->query("SELECT id_formato FROM libro_formato where id_libro = " . $id_libro . ";");
        while ($data = $oid->fetch_assoc()) {
            deleteFormato($data['id_formato']);
        }

        $mysqli->query("DELETE from recensisce where id_libro = " . $id_libro . ";");
        $mysqli->query("DELETE from scritto where id_libro = " . $id_libro . ";");
        $mysqli->query("DELETE from libro where id = " . $id_libro . ";");
    } else {
        //rendo libro e formati invisibili perché vengono utilizzati
        $mysqli->query("UPDATE libro set visibile = 0 where id = " . $id_libro . ";");

        $oid = $mysqli->query("SELECT id_formato FROM libro_formato where id_libro = " . $id_libro . ";");
        while ($data = $oid->fetch_assoc()) {
            $mysqli->query("DELETE from wishlist where id_formato = " . $data['id_formato'] . ";");
            $mysqli->query("UPDATE formato set visibile = 0 where id = " . $data['id_formato'] . ";");
        }
    }
}

//-----------------Ordine----------------------------------


function getOrdine()
{
    global $mysqli;
    return $mysqli->query("SELECT id, stato, indirizzo_consegna as indirizzo, ordine.data from ordine order by ordine.stato desc, ordine.data asc;");
}

function getCountNewOrdine()
{
    global $mysqli;

    $oid = $mysqli->query("SELECT COUNT(*) count FROM ordine WHERE ordine.stato = 'nuovo';");
    if ($oid) {
        $data = $oid->fetch_assoc();
        return $data['count'];
    }
    return 0;
}

function setIndirizzoOrdine($id_ordine, $indirizzo)
{
    global $mysqli;
    $mysqli->query("UPDATE ordine SET indirizzo_consegna = \"" . addslashes($indirizzo) . "\" WHERE id = " . $id_ordine . ";");
}

function setSpeditoOrdine($id_ordine)
{
    global $mysqli;
    $mysqli->query("UPDATE ordine SET stato = 'spedito' WHERE id = " . $id_ordine . ";");
}

//-----------------Recensione----------------------------------

function getRecensione()
{
    global $mysqli;
    return $mysqli->query("SELECT libro.titolo as libro, testa_giornalistica.nome, recensisce.recensione, recensisce.id_libro, recensisce.id_testa_giornalistica from recensisce 
            left join libro on recensisce.id_libro = libro.id left join testa_giornalistica on recensisce.id_testa_giornalistica = testa_giornalistica.id 
            order by testa_giornalistica.nome asc;");
}

function getRecensioneByID()
{
    global $mysqli;
    return $mysqli->query("SELECT recensione FROM recensisce WHERE id_libro = " . $_SESSION['recensione'][0] . " AND id_testa_giornalistica = " . $_SESSION['recensione'][1] . ";");
}

function setRecensione($id_libro, $id_testata_giornalistica, $recensione)
{
    global $mysqli;

    $oid = $mysqli->query("SELECT * FROM `recensisce` r WHERE r.id_libro =  '{$id_libro}' AND r.id_testa_giornalistica = '{$id_testata_giornalistica}' ;");

    $data = $oid->fetch_assoc();

    $quote = addslashes($recensione);

    if ($data['id_libro'] == null) {
        //sto creando una nuova recensione
        $mysqli->query("INSERT INTO `recensisce`( `id_testa_giornalistica` , `id_libro` , `recensione`)
                         VALUES ( '{$id_testata_giornalistica}' , '{$id_libro}', '{$quote}' );");
    } else {
        $mysqli->query("UPDATE `recensisce` r SET r.`recensione`= '{$quote}' 
                    WHERE r.id_libro =  '{$id_libro}' AND r.id_testa_giornalistica = '{$id_testata_giornalistica}'  ;");
    }
}

function deleteRecensione($id_testata_giornalistica)
{
    global $mysqli;
    $oid = $mysqli->query("SELECT id_libro FROM recensisce
                        WHERE id_testa_giornalistica = " . $id_testata_giornalistica . ";");

    //posso eliminare la testa_giornalistica con i suoi commenti
    while ($data = $oid->fetch_assoc()) {
        if (isset($data['id_libro']) and $data['id_libro'] > 0)
            $mysqli->query("DELETE FROM recensisce WHERE id_libro = " . $data['id_libro'] . "  AND id_testa_giornalistica = " . $id_testata_giornalistica . ";");
    }
}

function deleteRecensioneByIDLibro($id_testata_giornalistica, $id_libro)
{
    global $mysqli;
    $mysqli->query("DELETE from recensisce where id_libro = " . $id_libro . " AND id_testa_giornalistica = " . $id_testata_giornalistica . ";");
}

//-----------------Scritto----------------------------------

function setScritto($id_libro, $id_autore)
{
    global $mysqli;
    //libro già esistente con collegamento ad autore già esistente
    $oid = $mysqli->query("SELECT count(*) as count FROM scritto WHERE id_libro = " . $id_libro . ";");
    $data = $oid->fetch_assoc();

    //Se 0 non esiste la relazione tra libro ed autore, altrimenti esiste e bisogna aggiornarla
    if ($data['count'] == 0)
        $mysqli->query("INSERT INTO scritto (id_libro, id_autore) VALUES (" . $id_libro . ", " . $id_autore . ");");
    else
        $mysqli->query("UPDATE scritto SET id_libro = " . $id_libro . ", id_autore = " . $id_autore . " WHERE id_libro = " . $id_libro . ";");
}

//-----------------Servizio----------------------------------

function getServizio()
{
    global $mysqli;
    return $mysqli->query("SELECT servizi.id, servizi.script FROM servizi; ");
}

//-----------------Testata Giornalistica----------------------------------

function getTestataGiornalistica()
{
    global $mysqli;
    return $mysqli->query("SELECT * from testa_giornalistica order by nome asc;");
}

function getTestataGiornalisticaByID($id_testata_giornalistica)
{
    global $mysqli;
    return $mysqli->query("SELECT * from testa_giornalistica WHERE id = '.$id_testata_giornalistica.' order by nome asc;");
}

function getCountCommentiTestataGiornalistica($id_testata_giornalistica)
{
    global $mysqli;
    return $mysqli->query("SELECT COUNT(id_testa_giornalistica) as count FROM recensisce
                        WHERE recensisce.id_testa_giornalistica = " . $id_testata_giornalistica . ";");
}

function setTestataGiornalistica($id_testata_giornalistica, $nome)
{
    global $mysqli;
    if ($id_testata_giornalistica == 0) {
        //sto creando una nuova testata giornalistica
        $mysqli->query("INSERT INTO testa_giornalistica (nome) VALUES 
                            (\"" . addslashes($_REQUEST['nome']) . "\");");
    } else {
        $mysqli->query("UPDATE testa_giornalistica SET nome = \"" . $nome . "\" WHERE id = " . $id_testata_giornalistica . ";");
    }
}

function deleteTestataGiornalistica($id_testata_giornalistica)
{
    global $mysqli;
    $mysqli->query("DELETE FROM testa_giornalistica WHERE id = " . $id_testata_giornalistica . ";");
}

//-----------------Utente----------------------------------

function getUtenti()
{
    global $mysqli;
    return $mysqli->query("SELECT utente.id, utente.nome, utente.cognome, utente.email, utente.indirizzo, gruppo.nome as nomegruppo 
                FROM utente join utenti_gruppi on utente.id = utenti_gruppi.id_utente left join gruppo on gruppo.id = utenti_gruppi.id_gruppi order by gruppo.id DESC, utente.cognome ASC;");
}

function getUtentiVip()
{
    global $mysqli;
    return $mysqli->query("SELECT u.nome, u.cognome, g.nome as nomegruppo, utenti_gruppi.id_gruppi FROM utente u left join utenti_gruppi 
        on u.id = utenti_gruppi.id_utente left join gruppo g on g.id = utenti_gruppi.id_gruppi WHERE g.id != 1 ORDER BY g.id desc, u.cognome asc");
}

function getUtenteByID($id_utente)
{
    global $mysqli;
    return $mysqli->query("SELECT utente.nome, utente.cognome, utente.email, utente.indirizzo, gruppo.nome as nomegruppo, utenti_gruppi.id_gruppi 
                            FROM utente join utenti_gruppi on utenti_gruppi.id_utente = utente.id  left join gruppo on gruppo.id = utenti_gruppi.id_gruppi WHERE utente.id = " . $id_utente . ";");
}

function getUtenteByEmail()
{
    global $mysqli;
    return $mysqli->query("SELECT utente.nome, utente.cognome, utente.email, utente.indirizzo, utenti_gruppi.id_gruppi, gruppo.nome as nomegruppo
                FROM utente join utenti_gruppi on utenti_gruppi.id_utente = utente.id left join gruppo on gruppo.id = utenti_gruppi.id_gruppi WHERE utente.email = \"" . $_SESSION['email'] . "\";");
}

//-----------------Utenti_Gruppi----------------------------------

function getCountUtentiGruppi()
{
    global $mysqli;
    return $mysqli->query("SELECT count(*) as count FROM utenti_gruppi where id_gruppi = 1;");
}

function setUtenteGruppo($id_utente, $id_gruppo)
{
    global $mysqli;
    $mysqli->query("UPDATE utenti_gruppi SET id_gruppi = " . $id_gruppo . " WHERE id_utente = " . $id_utente . ";");
}


//-----------------Join varie----------------------------------

function getTitoloFromOrdine($id_ordine)
{
    global $mysqli;
    return $mysqli->query("SELECT libro.titolo FROM `composto` left join libro_formato on composto.id_formato = libro_formato.id_formato 
                         join libro on libro_formato.id_libro = libro.id WHERE composto.id_ordine = " . $id_ordine . ";");
}

function getLibroAutoreCategoria()
{
    global $mysqli;
    return $mysqli->query("SELECT libro.id, libro.titolo, libro.descrizione, libro.immagine, libro.specifiche,
                autore.nome, autore.cognome, categoria.nome as categoria
                FROM libro left join scritto ON scritto.id_libro = libro.id 
                left join autore on scritto.id_autore = autore.id 
                left join appartiene on appartiene.id_libro = libro.id 
                left join categoria on appartiene.id_categoria = categoria.id 
                WHERE libro.visibile = 1 order by libro.id asc; ");
}

function getFatturatoMensile()
{
    global $mysqli;
    return $mysqli->query("SELECT sum(prezzo) as sommaprezzi FROM formato left join composto on composto.id_formato = formato.id
                            join ordine on ordine.id = composto.id_ordine where stato = 'spedito' and MONTH(data) = Month(CURRENT_DATE)");
}
