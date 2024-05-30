<?php

function notificaOK($msg)
{
    $t = new Template("skins/html_libro/utility/notificaok.html");
    $t->setContent("ph", $msg);
    return $t->close();
}

function notificaNOK($msg)
{
    $t = new Template("skins/html_libro/utility/notificanok.html");
    $t->setContent("ph", $msg);
    return $t->close();
}



function controllaAccesso()
{
    if(isset($_SESSION['email']) AND isset($_SESSION['password'])) {
        $email = $_SESSION['email'];
        $pass = $_SESSION['password'];
        if ($email != null && $pass != null) {
            $c = crypto($pass);

            global $mysqli;

            $oid = $mysqli->query(" SELECT * FROM `utente` u WHERE u.email='{$email}' and u.password='{$c}'; ");

            $data = $oid->fetch_assoc();

            if (!$oid AND isset($data) AND $data['email'] == null) {
                return false;
            }
            return true;
        }
    }
    return false;
}

function controllaAccessoEIPermessi($script)
{
    $email = $_SESSION['email'];
    $pass = $_SESSION['password'];
    if ($email != null && $pass != null ) {
        $c = crypto($pass);

        global $mysqli;

        $oid = $mysqli->query(" SELECT u.email FROM `utente` u join utenti_gruppi up on (up.id_utente = u.id) join gruppi_servizi gs on (gs.id_gruppo = up.id_gruppi ) join servizi s on (s.id = gs.id_servizi ) WHERE ( u.email='{$email}' and u.password='{$c}' and s.script = '{$script}' ) ; ");

        $data = $oid->fetch_assoc();

        if ($data['email'] == null) {
            Header("Location: listalibri.php?messaggio=errore non hai permessi ". $script );
            exit;
        }
        return true;
    }
    Header("Location: listalibri.php?messaggio=errore non hai permessi");
    exit;
}

function controllaPermessi($script)
{
    $email = $_SESSION['email'];
    $pass = $_SESSION['password'];
    if ($email != null && $pass != null ) {
        $c = crypto($pass);

        global $mysqli;

        $oid = $mysqli->query(" SELECT u.email FROM `utente` u join utenti_gruppi up on (up.id_utente = u.id) join gruppi_servizi gs on (gs.id_gruppo = up.id_gruppi ) join servizi s on (s.id = gs.id_servizi ) WHERE ( u.email='{$email}' and u.password='{$c}' and s.script = '{$script}' ) ; ");

        $data = $oid->fetch_assoc();

        if (isset($data['email'])) {
            return true;
            exit;
        }
        
    }
    return false;
    exit;
}

function controllaPermessiGruppi($id_gruppo, $id_script)
{
    //estrapola 
    global $mysqli;
    $oid = $mysqli->query(" SELECT id FROM servizi WHERE id IN (
        SELECT id_servizi FROM gruppi_servizi WHERE id_gruppo = ".$id_gruppo."); ");
   
    while($data = $oid->fetch_assoc()) {
        if($data['id'] == $id_script) {
            return 1;
            exit;
        }
    }
    return 0;
    exit;
}

function crypto($pass)
{
    return md5(md5($pass));
}


/// se le funzioni danno errore seguire la giuda qui di sotto
/// https://www.webfulcreations.com/call-to-undefined-function-imagecreatefromjpeg/
/// comando bash php --ri gd
function ridimensionaImmagine($immagineSource, $pixelW, $pixelH)
{
    $cpImmagineSource = substr($immagineSource, 0);
    $cpImmagineSource .= "";

    list($w, $h) = getimagesize($immagineSource);
    if ($w != $pixelW &&  $h != $pixelH) {
        
        $pathimg = substr($immagineSource, 0, strlen($immagineSource) - 4) . $pixelH . 'x' . $pixelW . '.jpg';
        if (file_exists($pathimg)) {
            return  $pathimg;
        }
        $type = strtolower(substr(strrchr($cpImmagineSource,"."),1));
        if($type == 'jpeg') $type = 'jpg';
        switch($type){
          case 'bmp': $img = imagecreatefromwbmp($cpImmagineSource); break;
          case 'gif': $img = imagecreatefromgif($cpImmagineSource); break;
          case 'jpg': $img = imagecreatefromjpeg($cpImmagineSource); break;
          case 'png': $img = imagecreatefrompng($cpImmagineSource); break;
          default : return "Unsupported picture type!";
        }

        $dst = imagecreatetruecolor($pixelW, $pixelH);
        imagecopyresampled($dst, $img, 0, 0, 0, 0, $pixelW, $pixelH, $w, $h);

        switch($type){
            case 'bmp': imagewbmp($dst, $pathimg); break;
            case 'gif': imagegif($dst, $pathimg); break;
            case 'jpg': imagejpeg($dst, $pathimg); break;
            case 'png': imagepng($dst, $pathimg); break;
          }
        return  $pathimg;
    }
    return $cpImmagineSource;
}