-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Creato il: Ago 28, 2024 alle 12:48
-- Versione del server: 10.4.32-MariaDB
-- Versione PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `webshopdb`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `caratteristica`
--

CREATE TABLE `caratteristica` (
  `id` int(11) NOT NULL,
  `nome` varchar(32) NOT NULL,
  `idCategoria` int(11) NOT NULL,
  `valoriDefault` varchar(512) NOT NULL,
  `versione` tinyint(3) UNSIGNED NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `caratteristica`
--

INSERT INTO `caratteristica` (`id`, `nome`, `idCategoria`, `valoriDefault`, `versione`) VALUES
(1, 'Memoria ROM', 1, '256Gb,512Gb,1Tb,2Tb,Indifferent', 2),
(2, 'Memoria RAM', 1, '2Gb,4Gb,8Gb,16Gb,32Gb,Indifferent', 2),
(3, 'Schermo', 2, '12\",14\",16\",Indifferent', 2),
(10, 'Ventole', 10, 'Tanta,Poca,Indifferent', 1),
(12, 'Dita', 12, '1,2,3,4,Indifferent', 1);

-- --------------------------------------------------------

--
-- Struttura della tabella `categoria`
--

CREATE TABLE `categoria` (
  `id` int(11) NOT NULL,
  `nome` varchar(64) NOT NULL,
  `idCategoriaPadre` int(11) DEFAULT NULL,
  `idImmagine` int(11) NOT NULL,
  `eliminato` tinyint(1) NOT NULL DEFAULT 0,
  `versione` tinyint(3) UNSIGNED NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `categoria`
--

INSERT INTO `categoria` (`id`, `nome`, `idCategoriaPadre`, `idImmagine`, `eliminato`, `versione`) VALUES
(1, 'Pc', NULL, 1, 0, 2),
(2, 'Portatile', 1, 2, 0, 2),
(10, 'Boss', NULL, 9, 0, 1),
(12, 'Pizza', NULL, 11, 0, 1);

-- --------------------------------------------------------

--
-- Struttura della tabella `gruppo`
--

CREATE TABLE `gruppo` (
  `id` int(11) NOT NULL,
  `nome` varchar(16) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `gruppo`
--

INSERT INTO `gruppo` (`id`, `nome`) VALUES
(1, 'AMMINISTRATORE'),
(2, 'ORDINANTE'),
(3, 'TECNICO');

-- --------------------------------------------------------

--
-- Struttura della tabella `immagine`
--

CREATE TABLE `immagine` (
  `id` int(11) NOT NULL,
  `titolo` varchar(255) NOT NULL,
  `tipo` varchar(32) NOT NULL,
  `nomeFile` varchar(255) NOT NULL,
  `grandezza` int(11) NOT NULL,
  `versione` tinyint(3) UNSIGNED NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `immagine`
--

INSERT INTO `immagine` (`id`, `titolo`, `tipo`, `nomeFile`, `grandezza`, `versione`) VALUES
(1, 'Pc image', 'image/png', 'product1.png', 19705, 2),
(2, 'Portatile image', 'image/png', 'client.png', 34259, 2),
(9, 'Boss image', 'image/png', 'boss.png', 29076, 1),
(11, 'Pizza image', 'image/png', 'loser.png', 43690, 1);

-- --------------------------------------------------------

--
-- Struttura della tabella `notifica`
--

CREATE TABLE `notifica` (
  `id` int(11) NOT NULL,
  `idDestinatario` int(11) NOT NULL,
  `messaggio` varchar(512) NOT NULL,
  `link` varchar(128) NOT NULL,
  `tipo` enum('INFO','NUOVO','MODIFICATO','CHIUSO','ANNULLATO') NOT NULL,
  `dataCreazione` datetime NOT NULL DEFAULT current_timestamp(),
  `letto` tinyint(1) NOT NULL DEFAULT 0,
  `versione` tinyint(3) UNSIGNED NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `notifica`
--

INSERT INTO `notifica` (`id`, `idDestinatario`, `messaggio`, `link`, `tipo`, `dataCreazione`, `letto`, `versione`) VALUES
(1, 21, 'We\'re sorry, you\'re not allowed anymore to stay in Webshop. Bye!', '', 'INFO', '2024-08-21 13:56:36', 0, 1),
(2, 21, 'Welcome in Webshop, new client!', '', 'INFO', '2024-08-21 13:56:43', 0, 1),
(3, 22, 'Great news! Your request Poratile super performante has been taken in charge by one of our operators!', '', 'INFO', '2024-08-21 19:19:15', 0, 1),
(4, 20, 'Welcome in Webshop, new technician!', 'profile', 'INFO', '2024-08-28 11:39:37', 0, 1),
(5, 21, 'We\'re sorry, you\'re not allowed anymore to stay in Webshop. Bye!', '', 'INFO', '2024-08-28 11:44:49', 0, 1),
(6, 23, 'Welcome in Webshop, new client!', 'index', 'INFO', '2024-08-28 11:48:22', 0, 1),
(7, 21, 'Welcome in Webshop, new client!', 'index', 'INFO', '2024-08-28 11:48:33', 0, 1);

-- --------------------------------------------------------

--
-- Struttura della tabella `proposta`
--

CREATE TABLE `proposta` (
  `id` int(11) NOT NULL,
  `idRichiesta` int(11) NOT NULL,
  `idTecnico` int(11) NOT NULL,
  `nomeProdotto` varchar(64) NOT NULL,
  `nomeProduttore` varchar(64) NOT NULL,
  `descrizioneProdotto` varchar(1024) NOT NULL,
  `prezzoProdotto` float NOT NULL,
  `url` varchar(512) DEFAULT NULL,
  `note` varchar(1024) DEFAULT NULL,
  `dataCreazione` datetime NOT NULL DEFAULT current_timestamp(),
  `statoProposta` enum('INATTESA','APPROVATO','RESPINTO') NOT NULL DEFAULT 'INATTESA',
  `motivazione` varchar(1024) NOT NULL,
  `versione` tinyint(3) UNSIGNED NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struttura della tabella `richiesta`
--

CREATE TABLE `richiesta` (
  `id` int(11) NOT NULL,
  `titolo` varchar(64) NOT NULL,
  `descrizione` varchar(511) NOT NULL,
  `idCategoria` int(11) NOT NULL,
  `idOrdinante` int(11) NOT NULL,
  `idTecnico` int(11) DEFAULT NULL,
  `statoRichiesta` enum('NUOVO','PRESOINCARICO','ORDINATO','CHIUSO','ANNULLATO') NOT NULL DEFAULT 'NUOVO',
  `statoOrdine` enum('SPEDITO','ACCETTATO','RESPINTONONCONFORME','RESPINTONONFUNZIONANTE','EMPTY') NOT NULL,
  `dataCreazione` date NOT NULL,
  `note` varchar(1024) DEFAULT NULL,
  `versione` tinyint(3) UNSIGNED NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `richiesta`
--

INSERT INTO `richiesta` (`id`, `titolo`, `descrizione`, `idCategoria`, `idOrdinante`, `idTecnico`, `statoRichiesta`, `statoOrdine`, `dataCreazione`, `note`, `versione`) VALUES
(1, 'Poratile super performante', 'Vorrei un pc portatile che non si riscalda mai neanche se gioco in un forno', 2, 22, 20, 'PRESOINCARICO', 'EMPTY', '2024-08-21', 'Lo voglio fiammeggiante', 2);

-- --------------------------------------------------------

--
-- Struttura della tabella `richiesta_caratteristica`
--

CREATE TABLE `richiesta_caratteristica` (
  `id` int(11) NOT NULL,
  `idRichiesta` int(11) NOT NULL,
  `idCaratteristica` int(11) NOT NULL,
  `valore` varchar(255) NOT NULL,
  `versione` tinyint(3) UNSIGNED NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `richiesta_caratteristica`
--

INSERT INTO `richiesta_caratteristica` (`id`, `idRichiesta`, `idCaratteristica`, `valore`, `versione`) VALUES
(1, 1, 1, '2Tb', 1),
(2, 1, 2, '16Gb', 1),
(3, 1, 3, '16\"', 1);

-- --------------------------------------------------------

--
-- Struttura della tabella `utente`
--

CREATE TABLE `utente` (
  `id` int(11) NOT NULL,
  `username` varchar(128) DEFAULT NULL,
  `email` varchar(128) NOT NULL,
  `password` varchar(256) NOT NULL,
  `indirizzo` varchar(256) NOT NULL,
  `dataIscrizione` date NOT NULL DEFAULT current_timestamp(),
  `accettato` tinyint(1) NOT NULL DEFAULT 0,
  `versione` tinyint(3) UNSIGNED NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `utente`
--

INSERT INTO `utente` (`id`, `username`, `email`, `password`, `indirizzo`, `dataIscrizione`, `accettato`, `versione`) VALUES
(1, 'Marko', 'durbanomk@gmail.com', '8cc8d85ae3084a966c24f7c61683a42675247c0f7f4d19138b8470e803a0701bee581b06a5062c5d28811c410151c618', 'Via pazzi, 2,  Tokyo,  123, Italy(IT)', '2024-08-06', 0, 15),
(20, 'Paperino', 'paperino@gmail.com', 'ebe1b163e84668a295c4d4e0a5463ff0889f6aa4f9f67a43a1849ac496d8ee4f7a8cfa7bd92a18027031c571c9cdab6e', 'Via papere, 313, Paperopoli, 55120, Afghanistan(AF)', '2024-08-07', 1, 1),
(21, 'Paperoga', 'paperoga@gmail.com', 'a5a2fe8fc3a262cef3c5f80dd5e0c40e969ef99ff2f61f083938c30f246c439bdc9ca5d0ecbc4d48e2d7b5532ddbcf7d', 'Via papere, 777, Paperopoli, 55241, Denmark(DK)', '2024-08-07', 1, 11),
(22, 'Zio Paperone', 'paperone@gmail.com', '3723936bd22d4c1932d09b61dd02db8d0012be4e56f1ad0562c81f51bc6bdb103ba3e1e068c6ba5bfa115db14ffb2874', 'Via ricconi, 1, Paperopoli, 55124, Barbados(BB)', '2024-08-07', 1, 11),
(23, 'Topolino', 'topolino@gmail.com', '16c8baf7852f0b93f3303fd810e98e70fb62625ac56294e2ff6ffe228636d064b92288eb68ce81793b2c8626c25a85e6', 'Via topa, 69, Topolinia, 74254, United States(US)', '2024-08-08', 1, 11),
(24, 'Topolina', 'topolina@gmail.com', '76a516535893d7a81ecebcb4c51416744d4655c8e75bf06de39f9d9824a8412fe499300a8738c721be7909479d5d01c6', 'Via Appia, 98, Napoli, 44150, Afghanistan(AF)', '2024-08-08', 1, 9);

-- --------------------------------------------------------

--
-- Struttura della tabella `utente_gruppo`
--

CREATE TABLE `utente_gruppo` (
  `idUtente` int(11) NOT NULL,
  `idGruppo` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `utente_gruppo`
--

INSERT INTO `utente_gruppo` (`idUtente`, `idGruppo`) VALUES
(1, 1),
(20, 3),
(21, 2),
(22, 2),
(23, 2),
(24, 2);

--
-- Indici per le tabelle scaricate
--

--
-- Indici per le tabelle `caratteristica`
--
ALTER TABLE `caratteristica`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nome` (`nome`),
  ADD KEY `categoria` (`idCategoria`) USING BTREE;

--
-- Indici per le tabelle `categoria`
--
ALTER TABLE `categoria`
  ADD PRIMARY KEY (`id`),
  ADD KEY `categoria_FK2` (`idImmagine`),
  ADD KEY `categoria_FK1` (`idCategoriaPadre`);

--
-- Indici per le tabelle `gruppo`
--
ALTER TABLE `gruppo`
  ADD PRIMARY KEY (`id`);

--
-- Indici per le tabelle `immagine`
--
ALTER TABLE `immagine`
  ADD PRIMARY KEY (`id`);

--
-- Indici per le tabelle `notifica`
--
ALTER TABLE `notifica`
  ADD PRIMARY KEY (`id`),
  ADD KEY `notifica_FK1` (`idDestinatario`);

--
-- Indici per le tabelle `proposta`
--
ALTER TABLE `proposta`
  ADD PRIMARY KEY (`id`),
  ADD KEY `proposta_FK2` (`idTecnico`),
  ADD KEY `id_richiesta` (`idRichiesta`,`idTecnico`);

--
-- Indici per le tabelle `richiesta`
--
ALTER TABLE `richiesta`
  ADD PRIMARY KEY (`id`),
  ADD KEY `richiesta_FK1` (`idCategoria`),
  ADD KEY `richiesta_FK2` (`idOrdinante`),
  ADD KEY `richiesta_FK3` (`idTecnico`);

--
-- Indici per le tabelle `richiesta_caratteristica`
--
ALTER TABLE `richiesta_caratteristica`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `id_richiesta` (`idRichiesta`,`idCaratteristica`),
  ADD KEY `richiesta_caratteristica_FK2` (`idCaratteristica`),
  ADD KEY `id_richiesta_2` (`idRichiesta`);

--
-- Indici per le tabelle `utente`
--
ALTER TABLE `utente`
  ADD PRIMARY KEY (`id`);

--
-- Indici per le tabelle `utente_gruppo`
--
ALTER TABLE `utente_gruppo`
  ADD KEY `utente_gruppo_FK2` (`idGruppo`),
  ADD KEY `utente_gruppo_FK1` (`idUtente`);

--
-- AUTO_INCREMENT per le tabelle scaricate
--

--
-- AUTO_INCREMENT per la tabella `caratteristica`
--
ALTER TABLE `caratteristica`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT per la tabella `categoria`
--
ALTER TABLE `categoria`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT per la tabella `gruppo`
--
ALTER TABLE `gruppo`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT per la tabella `immagine`
--
ALTER TABLE `immagine`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT per la tabella `notifica`
--
ALTER TABLE `notifica`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT per la tabella `proposta`
--
ALTER TABLE `proposta`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `richiesta`
--
ALTER TABLE `richiesta`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT per la tabella `richiesta_caratteristica`
--
ALTER TABLE `richiesta_caratteristica`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT per la tabella `utente`
--
ALTER TABLE `utente`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- Limiti per le tabelle scaricate
--

--
-- Limiti per la tabella `caratteristica`
--
ALTER TABLE `caratteristica`
  ADD CONSTRAINT `caratteristica_FK1` FOREIGN KEY (`idCategoria`) REFERENCES `categoria` (`id`) ON DELETE CASCADE;

--
-- Limiti per la tabella `categoria`
--
ALTER TABLE `categoria`
  ADD CONSTRAINT `categoria_FK1` FOREIGN KEY (`idCategoriaPadre`) REFERENCES `categoria` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `categoria_FK2` FOREIGN KEY (`idImmagine`) REFERENCES `immagine` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Limiti per la tabella `notifica`
--
ALTER TABLE `notifica`
  ADD CONSTRAINT `notifica_FK1` FOREIGN KEY (`idDestinatario`) REFERENCES `utente` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Limiti per la tabella `proposta`
--
ALTER TABLE `proposta`
  ADD CONSTRAINT `proposta_FK1` FOREIGN KEY (`idRichiesta`) REFERENCES `richiesta` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `proposta_FK2` FOREIGN KEY (`idTecnico`) REFERENCES `utente` (`id`) ON UPDATE CASCADE;

--
-- Limiti per la tabella `richiesta`
--
ALTER TABLE `richiesta`
  ADD CONSTRAINT `richiesta_FK1` FOREIGN KEY (`idCategoria`) REFERENCES `categoria` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `richiesta_FK2` FOREIGN KEY (`idOrdinante`) REFERENCES `utente` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `richiesta_FK3` FOREIGN KEY (`idTecnico`) REFERENCES `utente` (`id`) ON UPDATE CASCADE;

--
-- Limiti per la tabella `richiesta_caratteristica`
--
ALTER TABLE `richiesta_caratteristica`
  ADD CONSTRAINT `richiesta_caratteristica_FK1` FOREIGN KEY (`idRichiesta`) REFERENCES `richiesta` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `richiesta_caratteristica_FK2` FOREIGN KEY (`idCaratteristica`) REFERENCES `caratteristica` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Limiti per la tabella `utente_gruppo`
--
ALTER TABLE `utente_gruppo`
  ADD CONSTRAINT `utente_gruppo_FK1` FOREIGN KEY (`idUtente`) REFERENCES `utente` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `utente_gruppo_FK2` FOREIGN KEY (`idGruppo`) REFERENCES `gruppo` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
