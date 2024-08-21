-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Creato il: Ago 20, 2024 alle 17:52
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

-- --------------------------------------------------------

--
-- Struttura della tabella `categoria`
--

CREATE TABLE `categoria` (
  `id` int(11) NOT NULL,
  `nome` varchar(64) NOT NULL,
  `idCategoriaPadre` int(11) DEFAULT NULL,
  `idImmagine` int(11) NOT NULL,
  `versione` tinyint(3) UNSIGNED NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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
-- Struttura della tabella `gruppo_servizio`
--

CREATE TABLE `gruppo_servizio` (
  `id_gruppo` int(11) NOT NULL,
  `id_servizio` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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
  `dataCreazione` date NOT NULL,
  `statoProposta` enum('INATTESA','APPROVATO','RESPINTO','') NOT NULL,
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
  `idTecnico` int(11) NOT NULL,
  `statoRichiesta` enum('NUOVO','PRESOINCARICO','ORDINATO','CHIUSO','ANNULLATO') NOT NULL DEFAULT 'NUOVO',
  `statoOrdine` enum('ACCETTATO','RESPINTONONCONFORME','RESPINTONONFUNZIONANTE','') NOT NULL,
  `dataCreazione` date NOT NULL,
  `note` varchar(1024) DEFAULT NULL,
  `versione` tinyint(3) UNSIGNED NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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

-- --------------------------------------------------------

--
-- Struttura della tabella `servizio`
--

CREATE TABLE `servizio` (
  `id` int(11) NOT NULL,
  `script` varchar(32) NOT NULL,
  `versione` tinyint(3) UNSIGNED NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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
(1, 'Pietro', 'durbanomk@gmail.com', '8cc8d85ae3084a966c24f7c61683a42675247c0f7f4d19138b8470e803a0701bee581b06a5062c5d28811c410151c618', 'Via pazzi, 2,  Tokyo,  123, Italy(IT)', '2024-08-06', 0, 14),
(20, 'Paperino', 'paperino@gmail.com', 'ebe1b163e84668a295c4d4e0a5463ff0889f6aa4f9f67a43a1849ac496d8ee4f7a8cfa7bd92a18027031c571c9cdab6e', 'Via papere, 313, Paperopoli, 55120, Afghanistan(AF)', '2024-08-07', 1, 1),
(21, 'Paperoga', 'paperoga@gmail.com', 'a5a2fe8fc3a262cef3c5f80dd5e0c40e969ef99ff2f61f083938c30f246c439bdc9ca5d0ecbc4d48e2d7b5532ddbcf7d', 'Via papere, 777, Paperopoli, 55241, Denmark(DK)', '2024-08-07', 1, 5),
(22, 'Zio Paperone', 'paperone@gmail.com', '3723936bd22d4c1932d09b61dd02db8d0012be4e56f1ad0562c81f51bc6bdb103ba3e1e068c6ba5bfa115db14ffb2874', 'Via ricconi, 1, Paperopoli, 55124, Barbados(BB)', '2024-08-07', 0, 10),
(23, 'Topolino', 'topolino@gmail.com', '16c8baf7852f0b93f3303fd810e98e70fb62625ac56294e2ff6ffe228636d064b92288eb68ce81793b2c8626c25a85e6', 'Via topa, 69, Topolinia, 74254, United States(US)', '2024-08-08', 0, 10),
(24, 'Topolina', 'topolina@gmail.com', '76a516535893d7a81ecebcb4c51416744d4655c8e75bf06de39f9d9824a8412fe499300a8738c721be7909479d5d01c6', 'Via Appia, 98, Napoli, 44150, Afghanistan(AF)', '2024-08-08', 1, 9),
(25, 'Pippo', 'pippo@gmail.com', '05a243172fa9edddfc6b95e4926d8a80b0d90e2ec6af3e2b9612a24109d2c00ed0d31291c1601171c7f4d7fc53ce50fe', 'Via da Cane, 9, Pippopoli, 89542, United States(US)', '2024-08-08', 1, 12);

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
(20, 2),
(21, 2),
(22, 2),
(23, 2),
(24, 2),
(25, 2);

--
-- Indici per le tabelle scaricate
--

--
-- Indici per le tabelle `caratteristica`
--
ALTER TABLE `caratteristica`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nome` (`nome`),
  ADD UNIQUE KEY `categoria` (`idCategoria`);

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
-- Indici per le tabelle `gruppo_servizio`
--
ALTER TABLE `gruppo_servizio`
  ADD UNIQUE KEY `id_gruppo` (`id_gruppo`,`id_servizio`),
  ADD KEY `gruppo_servizio_FK2` (`id_servizio`);

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
-- Indici per le tabelle `servizio`
--
ALTER TABLE `servizio`
  ADD PRIMARY KEY (`id`);

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `categoria`
--
ALTER TABLE `categoria`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `gruppo`
--
ALTER TABLE `gruppo`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT per la tabella `immagine`
--
ALTER TABLE `immagine`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `notifica`
--
ALTER TABLE `notifica`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `proposta`
--
ALTER TABLE `proposta`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `richiesta`
--
ALTER TABLE `richiesta`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `richiesta_caratteristica`
--
ALTER TABLE `richiesta_caratteristica`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `servizio`
--
ALTER TABLE `servizio`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

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
-- Limiti per la tabella `gruppo_servizio`
--
ALTER TABLE `gruppo_servizio`
  ADD CONSTRAINT `gruppo_servizio_FK1` FOREIGN KEY (`id_gruppo`) REFERENCES `gruppo` (`id`),
  ADD CONSTRAINT `gruppo_servizio_FK2` FOREIGN KEY (`id_servizio`) REFERENCES `servizio` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

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
