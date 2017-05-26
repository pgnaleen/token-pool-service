USE `token_service`;
--
-- Table structure for table `tstemail`
--

DROP TABLE IF EXISTS `tstemail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tstemail` (
  `idtstemail` int(11) NOT NULL AUTO_INCREMENT,
  `tsxwhodid` int(11) NOT NULL,
  `tstmailaddr` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`idtstemail`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;



ALTER TABLE `tsxwho` ADD ( `reattmptcount` int(3) DEFAULT NULL,
  `retrymax` int(3) DEFAULT NULL,
 `retrydelay` int(11) DEFAULT NULL);



