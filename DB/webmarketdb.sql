-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Creato il: Lug 20, 2024 alle 20:06
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
-- Database: `webmarketdb`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `caratteristica`
--

CREATE TABLE `caratteristica` (
  `id` int(11) NOT NULL,
  `nome` varchar(16) NOT NULL,
  `id_categoria` int(11) NOT NULL,
  `valori_default` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struttura della tabella `categoria`
--

CREATE TABLE `categoria` (
  `id` int(11) NOT NULL,
  `nome` varchar(32) NOT NULL,
  `id_categoriaPadre` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struttura della tabella `gruppo`
--

CREATE TABLE `gruppo` (
  `id` int(11) NOT NULL,
  `nome` enum('AMMINISTRATORE','ORDINANTE','TECNICO') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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
-- Struttura della tabella `notifica`
--

CREATE TABLE `notifica` (
  `id` int(11) NOT NULL,
  `id_destinatario` int(11) NOT NULL,
  `messaggio` text NOT NULL,
  `data` date NOT NULL,
  `letto` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struttura della tabella `proposta_acquisto`
--

CREATE TABLE `proposta_acquisto` (
  `id` int(11) NOT NULL,
  `id_richiesta` int(11) NOT NULL,
  `id_tecnico` int(11) NOT NULL,
  `nome_prodotto` varchar(32) NOT NULL,
  `nome_produttore` varchar(32) NOT NULL,
  `descrizione_prodotto` text NOT NULL,
  `prezzo_prodotto` float NOT NULL,
  `url` text DEFAULT NULL,
  `note` text DEFAULT NULL,
  `data_creazione` date NOT NULL,
  `stato_proposta` enum('INATTESA','APPROVATO','RESPINTO') NOT NULL DEFAULT 'INATTESA',
  `motivazione` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struttura della tabella `richiesta_acquisto`
--

CREATE TABLE `richiesta_acquisto` (
  `id` int(11) NOT NULL,
  `titolo` varchar(64) NOT NULL,
  `descrizione` text NOT NULL,
  `id_categoria` int(11) NOT NULL,
  `id_ordinante` int(11) NOT NULL,
  `id_tecnico` int(11) NOT NULL,
  `stato_richiesta` enum('NUOVO','PRESOINCARICO','ORDINATO','CHIUSO') NOT NULL DEFAULT 'NUOVO',
  `stato_ordine` enum('INCORSO','ACCETTATO','RESPINTONONCONFORME','RESPINTONONFUNZIONANTE') NOT NULL DEFAULT 'INCORSO',
  `data_creazione` date NOT NULL,
  `note` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struttura della tabella `richiesta_caratteristica`
--

CREATE TABLE `richiesta_caratteristica` (
  `id_richiesta` int(11) NOT NULL,
  `id_caratteristica` int(11) NOT NULL,
  `valore` varchar(16) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struttura della tabella `servizio`
--

CREATE TABLE `servizio` (
  `id` int(11) NOT NULL,
  `script` varchar(16) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struttura della tabella `utente`
--

CREATE TABLE `utente` (
  `id` int(11) NOT NULL,
  `username` text DEFAULT NULL,
  `email` text NOT NULL,
  `password` text NOT NULL,
  `indirizzo` text NOT NULL,
  `accettato` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struttura della tabella `utente_gruppo`
--

CREATE TABLE `utente_gruppo` (
  `id_utente` int(11) NOT NULL,
  `id_gruppo` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indici per le tabelle scaricate
--

--
-- Indici per le tabelle `caratteristica`
--
ALTER TABLE `caratteristica`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nome` (`nome`),
  ADD UNIQUE KEY `categoria` (`id_categoria`);

--
-- Indici per le tabelle `categoria`
--
ALTER TABLE `categoria`
  ADD PRIMARY KEY (`id`),
  ADD KEY `categoria_FK1` (`id_categoria`);

--
-- Indici per le tabelle `gruppo`
--
ALTER TABLE `gruppo`
  ADD PRIMARY KEY (`id`);

--
-- Indici per le tabelle `gruppo_servizio`
--
ALTER TABLE `gruppo_servizio`
  ADD KEY `gruppo_servizio_FK1` (`id_gruppo`),
  ADD KEY `gruppo_servizio_FK2` (`id_servizio`);

--
-- Indici per le tabelle `notifica`
--
ALTER TABLE `notifica`
  ADD PRIMARY KEY (`id`),
  ADD KEY `notifica_FK1` (`id_destinatario`);

--
-- Indici per le tabelle `proposta_acquisto`
--
ALTER TABLE `proposta_acquisto`
  ADD PRIMARY KEY (`id`),
  ADD KEY `proposta_FK1` (`id_richiesta`),
  ADD KEY `proposta_FK2` (`id_tecnico`);

--
-- Indici per le tabelle `richiesta_acquisto`
--
ALTER TABLE `richiesta_acquisto`
  ADD PRIMARY KEY (`id`),
  ADD KEY `richiesta_FK1` (`id_categoria`),
  ADD KEY `richiesta_FK2` (`id_ordinante`),
  ADD KEY `richiesta_FK3` (`id_tecnico`);

--
-- Indici per le tabelle `richiesta_caratteristica`
--
ALTER TABLE `richiesta_caratteristica`
  ADD KEY `richiesta_caratteristica_FK1` (`id_richiesta`),
  ADD KEY `richiesta_caratteristica_FK2` (`id_caratteristica`);

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
  ADD KEY `utente_gruppo_FK1` (`id_utente`),
  ADD KEY `utente_gruppo_FK2` (`id_gruppo`);

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `notifica`
--
ALTER TABLE `notifica`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `proposta_acquisto`
--
ALTER TABLE `proposta_acquisto`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `richiesta_acquisto`
--
ALTER TABLE `richiesta_acquisto`
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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Limiti per le tabelle scaricate
--

--
-- Limiti per la tabella `caratteristica`
--
ALTER TABLE `caratteristica`
  ADD CONSTRAINT `caratteristica_FK1` FOREIGN KEY (`id_categoria`) REFERENCES `categoria` (`id`);

--
-- Limiti per la tabella `categoria`
--
ALTER TABLE `categoria`
  ADD CONSTRAINT `categoria_FK1` FOREIGN KEY (`id_categoria`) REFERENCES `categoria` (`id`);

--
-- Limiti per la tabella `gruppo_servizio`
--
ALTER TABLE `gruppo_servizio`
  ADD CONSTRAINT `gruppo_servizio_FK1` FOREIGN KEY (`id_gruppo`) REFERENCES `gruppo` (`id`),
  ADD CONSTRAINT `gruppo_servizio_FK2` FOREIGN KEY (`id_servizio`) REFERENCES `servizio` (`id`);

--
-- Limiti per la tabella `notifica`
--
ALTER TABLE `notifica`
  ADD CONSTRAINT `notifica_FK1` FOREIGN KEY (`id_destinatario`) REFERENCES `utente` (`id`);

--
-- Limiti per la tabella `proposta_acquisto`
--
ALTER TABLE `proposta_acquisto`
  ADD CONSTRAINT `proposta_FK1` FOREIGN KEY (`id_richiesta`) REFERENCES `richiesta_acquisto` (`id`),
  ADD CONSTRAINT `proposta_FK2` FOREIGN KEY (`id_tecnico`) REFERENCES `utente` (`id`);

--
-- Limiti per la tabella `richiesta_acquisto`
--
ALTER TABLE `richiesta_acquisto`
  ADD CONSTRAINT `richiesta_FK1` FOREIGN KEY (`id_categoria`) REFERENCES `categoria` (`id`),
  ADD CONSTRAINT `richiesta_FK2` FOREIGN KEY (`id_ordinante`) REFERENCES `utente` (`id`),
  ADD CONSTRAINT `richiesta_FK3` FOREIGN KEY (`id_tecnico`) REFERENCES `utente` (`id`);

--
-- Limiti per la tabella `richiesta_caratteristica`
--
ALTER TABLE `richiesta_caratteristica`
  ADD CONSTRAINT `richiesta_caratteristica_FK1` FOREIGN KEY (`id_richiesta`) REFERENCES `richiesta_acquisto` (`id`),
  ADD CONSTRAINT `richiesta_caratteristica_FK2` FOREIGN KEY (`id_caratteristica`) REFERENCES `caratteristica` (`id`);

--
-- Limiti per la tabella `utente_gruppo`
--
ALTER TABLE `utente_gruppo`
  ADD CONSTRAINT `utente_gruppo_FK1` FOREIGN KEY (`id_utente`) REFERENCES `utente` (`id`),
  ADD CONSTRAINT `utente_gruppo_FK2` FOREIGN KEY (`id_gruppo`) REFERENCES `gruppo` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
