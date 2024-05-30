<?php
/*   --------------------------
Upload image
 - in input necessita $_SESSION['sourceImage'] per definire la fonte di provenienza del file
 - gestisce automaticamente il caricamento di foto e immagini
 - carica l'immagine scelta in una cartella comune
 - rinomina il file caricato con un nome standard
 - controlla se il nome del file generato esiste già. Se é così lo rigenera
 - effettua controlli su estensione, dimensione foto e peso file
 - genera un messaggio di errore in output in $_SESSION['message'] 
 - ritorna la nuova posizione del file in $_SESSION['destImage']    
--------------------------*/

    unset($_SESSION['message']);
    $target_dir = "skins/assert_libro/images/";
    $src_img = $_SESSION['sourceImage'];
    $uploadOk = 1;

    //aggiungo destinazione immagine 
    $target_file = $target_dir . basename($_FILES[$src_img]["name"]);
    $imageFileType = strtolower(pathinfo($target_file,PATHINFO_EXTENSION));

    //dopo aver creato il nuovo nome controllo se è già stato utilizzato. Se è così lo rigenero
    do {
        $filename = $target_dir . "image_upld" . rand(10,999999) . "." . $imageFileType;
    } while (count(glob($filename)) > 0);
    
    if($_FILES[$src_img]['error'] == 0) {
        // Check if image file is a actual image or fake image
        if(isset($_POST["submit"])) {
            $check = getimagesize($_FILES[$src_img]["tmp_name"]);
            if($check !== false) {
                $uploadOk = 1;
            } else {
                $_SESSION['message'] = "File is not an image.";
                $uploadOk = 0;
            }
        }

        // Check if file already exists
        // Non lo considero come errore, ritorno direttamente la posizione del file
        if (file_exists($target_file)) {
            //$_SESSION['message'] = "Sorry, file already exists.";
            $_SESSION['destImage'] = $target_file;
            $uploadOk = 0;
        }

        // Check file size
        if ($_FILES[$src_img]["size"] > 5000000) {
            $_SESSION['message'] = "Sorry, your file is too large.";
            $uploadOk = 0;
        }

        // Allow certain file formats
        if($imageFileType != "jpg" && $imageFileType != "png" && $imageFileType != "jpeg"
        && $imageFileType != "gif" ) {
            $_SESSION['message'] = "Sorry, only JPG, JPEG, PNG & GIF files are allowed.";
            $uploadOk = 0;
        }

        // Check if $uploadOk is unset to 0 by an error, and try to upload file
        if ($uploadOk != 0) {

            if (move_uploaded_file($_FILES[$src_img]["tmp_name"], $filename)) {
                $_SESSION['destImage'] = $filename;
                $_SESSION['message'] = NULL;
            } else {
                $_SESSION['message'] = "Sorry, there was an error uploading your file.";
            }
        }
    } else {
        $_SESSION['message'] = "Any request selected";
    }
?>
