<?php

    // [name] = nome menù, [icon&color] = "fa fa-icona fa-colore", [link] = pagina da aprire

    //  Menu home - accessibile da amministratore e dipendente
    if(controllaPermessi('admin_home.php')) {
        $sidebar = new Template("skins/pluto/dtml/admin_sidebar.html");

        $sidebar->setContent("name", "Home");
        $sidebar->setContent("icon&color", "fa fa-home blue2_color");
        $sidebar->setContent("link", "admin_home.php");
        
        $main -> setContent("sidebar", $sidebar->get());
    }

    //  Menu gruppo - solo per amministratore
    if(controllaAccessoEIPermessi('alter_group.php')) {
        $sidebar = new Template("skins/pluto/dtml/admin_sidebar.html");

        $sidebar->setContent("name", "Gruppi");
        $sidebar->setContent("icon&color", "fa fa-group purple_color");
        $sidebar->setContent("link", "alter_group.php");

        $main -> setContent("sidebar", $sidebar->get());
    }

    //  Menu autore - se ho entrambi gli script mostro per amministratore altrimenti per dipendente
    if(controllaAccessoEIPermessi('author_edit.php') AND controllaAccessoEIPermessi('author_delete.php')) {
        $listasidebar = new Template("skins/pluto/dtml/admin_multiple_sidebar.html");
        
        $listasidebar->setContent("name", "Autori");
        $listasidebar->setContent("namelista", "autori");
        $listasidebar->setContent("icon&color", "fa fa-user purple_color2");
        $listasidebar->setContent("link1", "author_edit.php");
        $listasidebar->setContent("name1", "Aggiungi e modifica autore");
        $listasidebar->setContent("link2", "author_delete.php");
        $listasidebar->setContent("name2", "Elimina autore");

        $main -> setContent("sidebar", $listasidebar -> get());
    } else {
        
        if(controllaAccessoEIPermessi('book_edit.php')) {
            $sidebar = new Template("skins/pluto/dtml/admin_sidebar.html");

            $sidebar->setContent("name", "Aggiungi e modifica autore");
            $sidebar->setContent("icon&color", "fa fa-user purple_color2");
            $sidebar->setContent("link", "author_edit.php");
            
            $main -> setContent("sidebar", $sidebar -> get());
        }
    }

    //  Menu categoria - se ho entrambi gli script mostro per amministratore altrimenti per dipendente
    if(controllaAccessoEIPermessi('category_edit.php') AND controllaAccessoEIPermessi('category_delete.php')) {
        $listasidebar = new Template("skins/pluto/dtml/admin_multiple_sidebar.html");

        $listasidebar->setContent("name", "Categorie");
        $listasidebar->setContent("namelista", "categorie");
        $listasidebar->setContent("icon&color", "fa fa-star green_color");
        $listasidebar->setContent("link1", "category_edit.php");
        $listasidebar->setContent("name1", "Aggiungi e modifica categoria");
        $listasidebar->setContent("link2", "category_delete.php");
        $listasidebar->setContent("name2", "Elimina categoria");
        
        $main -> setContent("sidebar", $listasidebar -> get());
    } else {
        
        if(controllaAccessoEIPermessi('category_edit.php') AND !controllaAccessoEIPermessi('category_delete.php')) {
            $sidebar = new Template("skins/pluto/dtml/admin_sidebar.html");

            $sidebar->setContent("name", "Aggiungi e modifica categoria");
            $sidebar->setContent("icon&color", "fa fa-star green_color");
            $sidebar->setContent("link", "category_edit.php");
            
            $main -> setContent("sidebar", $sidebar -> get());
        }
    }

    //  Menu libro - se ho entrambi gli script mostro per amministratore altrimenti per dipendente
    if(controllaAccessoEIPermessi('book_edit.php') AND controllaAccessoEIPermessi('book_delete.php')) {
        $listasidebar = new Template("skins/pluto/dtml/admin_multiple_sidebar.html");
        
        $listasidebar->setContent("name", "Libri");
        $listasidebar->setContent("namelista", "libri");
        $listasidebar->setContent("icon&color", "fa fa-book");
        $listasidebar->setContent("link1", "book_edit.php");
        $listasidebar->setContent("name1", "Aggiungi e modifica libro");
        $listasidebar->setContent("link2", "book_delete.php");
        $listasidebar->setContent("name2", "Elimina libro");
        
        $main -> setContent("sidebar", $listasidebar->get());
    } else {
        
        if(controllaAccessoEIPermessi('book_edit.php') AND !controllaAccessoEIPermessi('book_delete.php')) {
            $sidebar = new Template("skins/pluto/dtml/admin_sidebar.html");

            $sidebar->setContent("name", "Aggiungi e modifica libro");
            $sidebar->setContent("icon&color", "fa fa-book ");
            $sidebar->setContent("link", "book_edit.php");

            $main -> setContent("sidebar", $sidebar->get());
        }
    }

    //  Menu formato - se ho entrambi gli script mostro per amministratore altrimenti per dipendente
    if(controllaAccessoEIPermessi('format_edit.php') AND controllaAccessoEIPermessi('format_delete.php')) {
        $listasidebar = new Template("skins/pluto/dtml/admin_multiple_sidebar.html");

        $listasidebar->setContent("name", "Formati");
        $listasidebar->setContent("namelista", "formati");
        $listasidebar->setContent("icon&color", "fa fa-bookmark brown_color");
        $listasidebar->setContent("link1", "format_edit.php");
        $listasidebar->setContent("name1", "Aggiungi e modifica formato");
        $listasidebar->setContent("link2", "format_delete.php");
        $listasidebar->setContent("name2", "Elimina formato");

        $main -> setContent("sidebar", $listasidebar -> get());
    } else {
        
        if(controllaAccessoEIPermessi('format_edit.php')) {
            $sidebar = new Template("skins/pluto/dtml/admin_sidebar.html");

            $sidebar->setContent("name", "Aggiungi e modifica formato");
            $sidebar->setContent("icon&color", "fa fa-user brown_color");
            $sidebar->setContent("link", "format_edit.php");
            
            $main -> setContent("sidebar", $sidebar -> get());
        }
    }

    //  Menu ordine - se ho entrambi gli script mostro per amministratore altrimenti per dipendente
    if(controllaAccessoEIPermessi('order_confirm.php') AND controllaAccessoEIPermessi('order_edit.php')) {
        $listasidebar = new Template("skins/pluto/dtml/admin_multiple_sidebar.html");

        $listasidebar->setContent("name", "Ordini");
        $listasidebar->setContent("namelista", "ordini");
        $listasidebar->setContent("icon&color", "fa fa-cubes yellow_color");
        $listasidebar->setContent("link1", "order_edit.php");
        $listasidebar->setContent("name1", "Modifica ordine");
        $listasidebar->setContent("link2", "order_confirm.php");
        $listasidebar->setContent("name2", "Conferma ordine");
        
        $main -> setContent("sidebar", $listasidebar -> get());
    } else {
        
        if(controllaAccessoEIPermessi('order_edit.php') AND !controllaAccessoEIPermessi('order_confirm.php')) {
            $sidebar = new Template("skins/pluto/dtml/admin_sidebar.html");

            $sidebar->setContent("name", "Modifica ordine");
            $sidebar->setContent("icon&color", "fa fa-cubes yellow_color");
            $sidebar->setContent("link", "order_edit.php");
            
            $main -> setContent("sidebar", $sidebar -> get());
        }
    }

    //  Menu testa giornalistica - se ho entrambi gli script mostro per amministratore altrimenti per dipendente
    if(controllaAccessoEIPermessi('testa_giornalistica_edit.php') AND controllaAccessoEIPermessi('testa_giornalistica_delete.php')) {
        $listasidebar = new Template("skins/pluto/dtml/admin_multiple_sidebar.html");

        $listasidebar->setContent("name", "Testate Giornalistiche");
        $listasidebar->setContent("namelista", "testate_giornalistiche");
        $listasidebar->setContent("icon&color", "fa fa-star green_color");
        $listasidebar->setContent("link1", "testa_giornalistica_edit.php");
        $listasidebar->setContent("name1", "Aggiungi e modifica testata");
        $listasidebar->setContent("link2", "testa_giornalistica_delete.php");
        $listasidebar->setContent("name2", "Elimina testata");
        
        $main -> setContent("sidebar", $listasidebar -> get());
    } else {
        
        if(controllaAccessoEIPermessi('testa_giornalistica_edit.php') AND !controllaAccessoEIPermessi('testa_giornalistica_delete.php')) {
            $sidebar = new Template("skins/pluto/dtml/admin_sidebar.html");

            $sidebar->setContent("name", "Aggiungi e modifica testata giornalistica");
            $sidebar->setContent("icon&color", "fa fa-star green_color");
            $sidebar->setContent("link", "testa_giornalistica_edit.php");
            
            $main -> setContent("sidebar", $sidebar -> get());
        }
    }

    //Menu commenti - per amministratore e dipendente
    if(controllaAccessoEIPermessi('comment.php')) {
        $listasidebar = new Template("skins/pluto/dtml/admin_multiple_sidebar.html");

        $listasidebar->setContent("name", "Commenti e recensioni");
        $listasidebar->setContent("namelista", "commenti_e_recensioni");
        $listasidebar->setContent("icon&color", "fa fa-comments-o red_color");
        $listasidebar->setContent("link1", "review_edit.php");
        $listasidebar->setContent("name1", "Aggiungi e modifica recensione");
        $listasidebar->setContent("link2", "comment.php");
        $listasidebar->setContent("name2", "Elimina commento o recensione");
        
        $main -> setContent("sidebar", $listasidebar -> get());
    }
