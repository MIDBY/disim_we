<?php
/*   --------------------------
Upload file
 - in input necessita $_SESSION['sourceFile'] per definire la fonte di provenienza del file
 - gestisce automaticamente il caricamento di file testo, audio, video
 - carica il file scelto in una cartella definita
 - rinomina il file caricato con un nome standard
 - controlla se il nome del file generato esiste già. Se é così lo rigenera
 - effettua controlli su estensione e peso file
 - genera un messaggio di errore in output in $_SESSION['message']  
 - ritorna la nuova posizione del file in $_SESSION['destFile']   
--------------------------*/

    unset($_SESSION['message']);
    $target_dir = "skins/assert_libro/files/";
    $src_file = $_SESSION['sourceFile'];       
    $uploadOk = 1;
    $correct_types = array ('aac', 'acsm', 'avi', 'azw', 'cbr', 'cbz', 'doc', 'docx', 'ePub', 'fb2', 'html', 'kpf', 'mobi', 'mp3', 'mp4', 'pdb', 'pdf', 'rtf', 'txt', 'wav', 'zip');

    if($_FILES[$src_file]['error'] == 0) {
        
        $target_file = $target_dir . basename($_FILES[$src_file]["name"]);    
        $imageFileType = strtolower(pathinfo($target_file,PATHINFO_EXTENSION));

        //dopo aver creato il nuovo nome controllo se è già stato utilizzato. Se è così lo rigenero
        do {
            $filename = $target_dir . "file_upld" . rand(10,999999) . "." . $imageFileType;
        } while (count(glob($filename)) > 0);

        // Check if file already exists
        // Non lo considero come errore, ritorno direttamente la posizione del file
        if (file_exists($target_file)) {
            //$_SESSION['message'] = "Sorry, file already exists.";
            $_SESSION['destImage'] = $target_file;
            $uploadOk = 0;
        }

        // Check file size less than 50 MB
        if ($_FILES[$src_file]["size"] > 50000000) {
            $_SESSION['message'] = "Sorry, your file is too large.";
            $uploadOk = 0;
        }

        // Allow certain file formats
        if(!in_array($imageFileType, $correct_types)) {
            $_SESSION['message'] = "Sorry, file format type not allowed.";
            $uploadOk = 0;
        }

        // Check if $uploadOk is unset to 0 by an error, and try to upload file
        if ($uploadOk != 0) {

            if (move_uploaded_file($_FILES[$src_file]["tmp_name"], $filename)) {
                $_SESSION['destFile'] = $filename;
                $_SESSION['message'] = NULL;
            } else {
                $_SESSION['message'] = "Sorry, there was an error uploading your file.";
            }
        }
    } else {
        $_SESSION['message'] = "Any request selected";
    }
?>
