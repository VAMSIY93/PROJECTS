/*
SQLyog Ultimate v8.55 
MySQL - 5.5.8 : Database - dbresultcenter
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`dbresultcenter` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `dbresultcenter`;

/*Table structure for table `tblallocatedstaff` */

DROP TABLE IF EXISTS `tblallocatedstaff`;

CREATE TABLE `tblallocatedstaff` (
  `intSrNo` int(10) NOT NULL AUTO_INCREMENT,
  `intStandard` int(5) NOT NULL,
  `strDiv` varchar(5) NOT NULL,
  `intSchoolID` mediumint(5) NOT NULL,
  `intAllocatedStaffID` tinyint(5) NOT NULL,
  PRIMARY KEY (`intSrNo`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

/*Data for the table `tblallocatedstaff` */

insert  into `tblallocatedstaff`(`intSrNo`,`intStandard`,`strDiv`,`intSchoolID`,`intAllocatedStaffID`) values (1,8,' A ',1,4),(3,3,'A',1,5);

/*Table structure for table `tblcity` */

DROP TABLE IF EXISTS `tblcity`;

CREATE TABLE `tblcity` (
  `intCityID` tinyint(10) NOT NULL AUTO_INCREMENT,
  `strCityName` varchar(30) NOT NULL,
  PRIMARY KEY (`intCityID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

/*Data for the table `tblcity` */

insert  into `tblcity`(`intCityID`,`strCityName`) values (1,'Gandhinagar'),(2,'Ahmedabad'),(3,'Rajkot'),(4,'jsdgjsfg');

/*Table structure for table `tblconvertedresult` */

DROP TABLE IF EXISTS `tblconvertedresult`;

CREATE TABLE `tblconvertedresult` (
  `intSrNo` int(10) NOT NULL AUTO_INCREMENT,
  `intExamID` mediumint(10) NOT NULL,
  `intStudentID` int(10) NOT NULL,
  `intSubjectID` int(10) NOT NULL,
  `intMarks` int(10) NOT NULL,
  `strStatus` varchar(10) NOT NULL,
  PRIMARY KEY (`intSrNo`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=latin1;

/*Data for the table `tblconvertedresult` */

insert  into `tblconvertedresult`(`intSrNo`,`intExamID`,`intStudentID`,`intSubjectID`,`intMarks`,`strStatus`) values (3,1,101,1,30,'fail'),(4,1,102,1,66,'pass'),(5,1,103,1,70,'pass'),(6,1,101,2,55,'pass'),(7,1,102,2,66,'pass'),(8,1,103,2,77,'pass'),(9,1,104,2,88,'pass'),(10,1,105,2,60,'pass'),(11,1,101,2,55,'pass'),(12,1,102,2,66,'pass'),(13,1,103,2,77,'pass'),(14,1,104,2,88,'pass'),(15,1,105,2,60,'pass'),(16,6,101,1,56,'pass'),(17,6,102,1,56,'pass'),(18,6,103,1,56,'pass'),(19,6,104,1,56,'pass'),(20,6,105,1,56,'pass');

/*Table structure for table `tbldivision` */

DROP TABLE IF EXISTS `tbldivision`;

CREATE TABLE `tbldivision` (
  `intDivID` tinyint(5) NOT NULL AUTO_INCREMENT,
  `strDiv` varchar(5) NOT NULL,
  PRIMARY KEY (`intDivID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

/*Data for the table `tbldivision` */

insert  into `tbldivision`(`intDivID`,`strDiv`) values (1,'A'),(2,'B');

/*Table structure for table `tblexams` */

DROP TABLE IF EXISTS `tblexams`;

CREATE TABLE `tblexams` (
  `intExamID` mediumint(10) NOT NULL AUTO_INCREMENT,
  `strExamTitle` varchar(30) NOT NULL,
  `dtmStartDate` date NOT NULL,
  `dtmEndDate` date NOT NULL,
  `intSchoolID` mediumint(10) NOT NULL,
  PRIMARY KEY (`intExamID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;

/*Data for the table `tblexams` */

insert  into `tblexams`(`intExamID`,`strExamTitle`,`dtmStartDate`,`dtmEndDate`,`intSchoolID`) values (1,' yearly exam','2012-12-12','2012-12-12',1),(2,' mid sem exam','2013-04-08','2013-04-12',1),(4,' final exam','2013-04-22','2013-04-26',1),(5,' asfdsd ','2013-04-11','2013-04-16',1),(6,' ksh ','2013-05-07','2013-05-11',1);

/*Table structure for table `tbllogin` */

DROP TABLE IF EXISTS `tbllogin`;

CREATE TABLE `tbllogin` (
  `intLoginID` mediumint(5) NOT NULL AUTO_INCREMENT,
  `strEmail` varchar(50) NOT NULL,
  `strPassword` varchar(15) NOT NULL,
  `intSecquestionID` tinyint(5) NOT NULL,
  `strAnswer` varchar(30) NOT NULL,
  `dtmRegDate` date NOT NULL,
  `dtmLastLoginDate` date NOT NULL,
  `strStatus` varchar(10) NOT NULL,
  `strUserType` varchar(10) NOT NULL,
  PRIMARY KEY (`intLoginID`,`strEmail`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=latin1;

/*Data for the table `tbllogin` */

insert  into `tbllogin`(`intLoginID`,`strEmail`,`strPassword`,`intSecquestionID`,`strAnswer`,`dtmRegDate`,`dtmLastLoginDate`,`strStatus`,`strUserType`) values (1,'shreyashkalaria@gmail.com','shreyash',1,'kdhfksdh','2012-12-12','2013-04-29','active','admin'),(2,'sanket.bhalodia@gmail.com','bhalodia',1,'eggdg','2012-12-12','2013-04-29','active','school'),(3,'chintan@gmail.com','chintan',1,'jgjg','2012-12-12','2012-12-12','block','school'),(11,' rohit@gmail.com ',' bhensdadia ',1,' 1234567890 ','2013-04-08','2013-04-19','block','staff'),(13,'kartik@gmail.com','kartik',1,'hgh','2011-12-12','2012-12-12','block','staff'),(14,' ronak@gmail.com ',' korvadia ',1,' 1234567890 ','2013-04-17','2013-04-29','active','staff'),(24,' a@g.vnv ',' asdfghjkl ',1,' shfgj ','2013-04-26','2013-04-26','block','staff'),(28,' vgec@gmail.com ',' shreyash ',1,' gfdsfs ','2013-04-28','2013-04-28','block','school'),(29,' skfh@fgh.jgf ',' asdfghjkl ',1,' 1234567890 ','2013-04-29','2013-04-29','active','school'),(30,' shrey@gmail.com ',' kalaria ',1,' 3432224 ','2013-04-29','2013-04-29',' block ','staff'),(31,' a@mail.com ',' asdfghjkl ',1,' 73248 ','2013-04-29','2013-04-29','active','staff'),(32,' shk@gmail.com ',' qwertyuiop ',1,' asjfg ','2013-04-29','2013-04-29',' block ','school');

/*Table structure for table `tblresultdates` */

DROP TABLE IF EXISTS `tblresultdates`;

CREATE TABLE `tblresultdates` (
  `intDateID` tinyint(10) NOT NULL AUTO_INCREMENT,
  `intExamID` mediumint(10) NOT NULL,
  `strDiv` varchar(10) NOT NULL,
  `intStandard` int(5) NOT NULL,
  `dtmDateofResult` date NOT NULL,
  `intLoginID` mediumint(10) NOT NULL,
  `dtmUpdatedDate` date NOT NULL,
  PRIMARY KEY (`intDateID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

/*Data for the table `tblresultdates` */

insert  into `tblresultdates`(`intDateID`,`intExamID`,`strDiv`,`intStandard`,`dtmDateofResult`,`intLoginID`,`dtmUpdatedDate`) values (1,1,'A',8,'2013-04-04',2,'2013-04-16'),(2,1,'B',8,'2013-04-05',2,'2013-04-16'),(3,2,'A',9,'2013-04-28',2,'2013-04-16'),(4,1,' A ',1,'2013-05-03',2,'2013-04-26'),(5,6,' A ',7,'2013-05-23',2,'2013-04-29');

/*Table structure for table `tblschool` */

DROP TABLE IF EXISTS `tblschool`;

CREATE TABLE `tblschool` (
  `intSchoolID` mediumint(10) NOT NULL AUTO_INCREMENT,
  `intLoginID` mediumint(10) NOT NULL,
  `strSchoolName` varchar(50) NOT NULL,
  `strSchoolAddress` varchar(100) NOT NULL,
  `intCityID` tinyint(10) NOT NULL,
  `strSchoolRegNo` varchar(15) NOT NULL,
  PRIMARY KEY (`intSchoolID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

/*Data for the table `tblschool` */

insert  into `tblschool`(`intSchoolID`,`intLoginID`,`strSchoolName`,`strSchoolAddress`,`intCityID`,`strSchoolRegNo`) values (1,2,'AZKaneria','khkhgkhk',1,'sad123'),(2,3,'K.O.Shah Adarsh School','khkhk',2,'kh22'),(3,28,' vgec ',' jgfhf ',1,'123'),(4,29,' kyyireyi ',' difiufdgy ',1,'123'),(5,32,' fjgtjgj ',' ktjg ',1,'jgdfjds');

/*Table structure for table `tblschoollogo` */

DROP TABLE IF EXISTS `tblschoollogo`;

CREATE TABLE `tblschoollogo` (
  `intSchoolID` int(5) NOT NULL,
  `strLogo` varchar(500) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `tblschoollogo` */

insert  into `tblschoollogo`(`intSchoolID`,`strLogo`) values (1,'E:\\JAVA\\JAVA\\Workspace\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp1\\wtpwebapps\\resultcenter\\image\\AZKaneria.jpg');

/*Table structure for table `tblschoolstudents` */

DROP TABLE IF EXISTS `tblschoolstudents`;

CREATE TABLE `tblschoolstudents` (
  `intStudentRegNo` int(10) NOT NULL AUTO_INCREMENT,
  `intGRNo` varchar(15) NOT NULL,
  `intSchoolID` mediumint(10) NOT NULL,
  `strFirstName` varchar(20) NOT NULL,
  `strLastName` varchar(20) NOT NULL,
  `strDivision` varchar(5) NOT NULL,
  `intStandard` int(5) NOT NULL,
  `strYear` varchar(10) NOT NULL,
  PRIMARY KEY (`intStudentRegNo`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=latin1;

/*Data for the table `tblschoolstudents` */

insert  into `tblschoolstudents`(`intStudentRegNo`,`intGRNo`,`intSchoolID`,`strFirstName`,`strLastName`,`strDivision`,`intStandard`,`strYear`) values (22,' 101 ',1,' Shreyash ',' Kalaria ',' A ',8,'1996'),(23,' 102 ',1,' Rohit ',' Bhensdadia ',' A ',8,'1996'),(24,' 103 ',1,' Jayesh ',' Vekaria ',' A ',8,'1996'),(25,' 104 ',1,' Sanket ',' Bhalodia ',' A ',8,'1996'),(26,' 105 ',1,' Jay ',' Kansagra ',' A ',8,'1996'),(32,' 110 ',1,' Milan ',' Kathrotia ',' B ',8,'1996'),(33,' 111 ',1,' Ravi ',' Ahuja ',' B ',8,'1996'),(34,' 112 ',1,' Nikunj ',' Malavia ',' B ',8,'1996'),(35,' 113 ',1,' Nirav ',' Bhadja ',' B ',8,'1996'),(36,' 114 ',1,' Chirag ',' Fefar ',' B ',8,'1996'),(37,' 1 ',1,' df ',' df ',' A ',10,'1'),(38,' 2 ',1,' sdf ',' df ',' A ',10,'1'),(39,' 3 ',1,' sdf ',' s ',' A ',10,'1'),(40,' 4 ',1,' fds ',' fs ',' A ',10,'1'),(41,' 5 ',1,' sfd ',' fs ',' A ',10,'1');

/*Table structure for table `tblsecquestion` */

DROP TABLE IF EXISTS `tblsecquestion`;

CREATE TABLE `tblsecquestion` (
  `intSecquestionID` tinyint(4) NOT NULL AUTO_INCREMENT,
  `strSecquestion` varchar(40) NOT NULL,
  PRIMARY KEY (`intSecquestionID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

/*Data for the table `tblsecquestion` */

insert  into `tblsecquestion`(`intSecquestionID`,`strSecquestion`) values (1,'What is your mobile no.?'),(3,'What is your vehical no.?'),(4,'In which city you born?');

/*Table structure for table `tblstaff` */

DROP TABLE IF EXISTS `tblstaff`;

CREATE TABLE `tblstaff` (
  `intStaffID` tinyint(5) NOT NULL AUTO_INCREMENT,
  `intSchoolID` mediumint(10) NOT NULL,
  `strFirstName` varchar(20) NOT NULL,
  `strLastName` varchar(20) NOT NULL,
  `intLoginID` mediumint(10) NOT NULL,
  `strMobileNo` varchar(15) NOT NULL,
  `strDesignation` varchar(10) NOT NULL,
  PRIMARY KEY (`intStaffID`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;

/*Data for the table `tblstaff` */

insert  into `tblstaff`(`intStaffID`,`intSchoolID`,`strFirstName`,`strLastName`,`intLoginID`,`strMobileNo`,`strDesignation`) values (1,2,'kartik','shiroya',13,'123456789','sdfgh'),(3,1,' rohit ',' bhensdadia ',11,' 1234567890 ','alhksjdhfk'),(4,1,' Ronak ',' Korvadia ',14,' 1234567890 ','asdf'),(5,1,' cbcbc ',' ggfgdg ',15,' 1234567890 ','asdfghj'),(6,1,' ksdhkj ',' kdhsg ',24,' 1111111111 ','sdfgh'),(7,3,' shreyash ',' kalaria ',30,' 9593457787 ','dsfg'),(8,1,' asfdgj ',' jsgdfj ',31,' 1111111111 ','hjdsg');

/*Table structure for table `tblstandard` */

DROP TABLE IF EXISTS `tblstandard`;

CREATE TABLE `tblstandard` (
  `intStandardID` int(5) NOT NULL AUTO_INCREMENT,
  `intStandard` int(5) NOT NULL,
  PRIMARY KEY (`intStandardID`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=latin1;

/*Data for the table `tblstandard` */

insert  into `tblstandard`(`intStandardID`,`intStandard`) values (1,1),(2,2),(3,3),(4,4),(5,5),(6,6),(7,7),(8,8),(9,9),(10,10),(11,11),(12,12);

/*Table structure for table `tblsubject` */

DROP TABLE IF EXISTS `tblsubject`;

CREATE TABLE `tblsubject` (
  `intSubID` int(10) NOT NULL AUTO_INCREMENT,
  `intSchoolID` mediumint(10) NOT NULL,
  `strSubjectName` varchar(30) NOT NULL,
  `strSubjectType` varchar(15) NOT NULL,
  `intStandard` mediumint(5) NOT NULL,
  PRIMARY KEY (`intSubID`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

/*Data for the table `tblsubject` */

insert  into `tblsubject`(`intSubID`,`intSchoolID`,`strSubjectName`,`strSubjectType`,`intStandard`) values (1,1,'Maths','compalsiry',8),(2,1,'Science','compalsiry',8),(3,2,'Maths','compalsiry',8),(4,2,'Science','compalsiry',8),(5,2,'Maths1',' optional ',8),(6,1,' gujarati ','compalsary',2),(7,1,' sdfgsjg ','compalsary',1);

/*Table structure for table `result` */

DROP TABLE IF EXISTS `result`;

/*!50001 DROP VIEW IF EXISTS `result` */;
/*!50001 DROP TABLE IF EXISTS `result` */;

/*!50001 CREATE TABLE  `result`(
 `intGRNo` varchar(15) ,
 `strFirstName` varchar(20) ,
 `strLastName` varchar(20) ,
 `strSchoolName` varchar(50) ,
 `strLogo` varchar(500) ,
 `intStandard` int(5) ,
 `strDivision` varchar(5) 
)*/;

/*Table structure for table `school` */

DROP TABLE IF EXISTS `school`;

/*!50001 DROP VIEW IF EXISTS `school` */;
/*!50001 DROP TABLE IF EXISTS `school` */;

/*!50001 CREATE TABLE  `school`(
 `intSchoolID` mediumint(10) ,
 `intLoginID` mediumint(5) ,
 `strSchoolName` varchar(50) ,
 `strSchoolAddress` varchar(100) ,
 `strSchoolRegNo` varchar(15) ,
 `strEmail` varchar(50) ,
 `strStatus` varchar(10) 
)*/;

/*Table structure for table `staff` */

DROP TABLE IF EXISTS `staff`;

/*!50001 DROP VIEW IF EXISTS `staff` */;
/*!50001 DROP TABLE IF EXISTS `staff` */;

/*!50001 CREATE TABLE  `staff`(
 `intStaffID` tinyint(5) ,
 `intLoginID` mediumint(5) ,
 `intSchoolID` mediumint(10) ,
 `strFirstName` varchar(20) ,
 `strLastName` varchar(20) ,
 `strEmail` varchar(50) ,
 `strMobileNo` varchar(15) ,
 `strDesignation` varchar(10) ,
 `strStatus` varchar(10) 
)*/;

/*View structure for view result */

/*!50001 DROP TABLE IF EXISTS `result` */;
/*!50001 DROP VIEW IF EXISTS `result` */;

/*!50001 CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `result` AS select `tblschoolstudents`.`intGRNo` AS `intGRNo`,`tblschoolstudents`.`strFirstName` AS `strFirstName`,`tblschoolstudents`.`strLastName` AS `strLastName`,`tblschool`.`strSchoolName` AS `strSchoolName`,`tblschoollogo`.`strLogo` AS `strLogo`,`tblschoolstudents`.`intStandard` AS `intStandard`,`tblschoolstudents`.`strDivision` AS `strDivision` from ((`tblconvertedresult` join `tblexams`) join ((`tblschool` join `tblschoollogo` on((`tblschool`.`intSchoolID` = `tblschoollogo`.`intSchoolID`))) join `tblschoolstudents` on((`tblschool`.`intSchoolID` = `tblschoolstudents`.`intSchoolID`)))) group by `tblschoolstudents`.`strLastName` */;

/*View structure for view school */

/*!50001 DROP TABLE IF EXISTS `school` */;
/*!50001 DROP VIEW IF EXISTS `school` */;

/*!50001 CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `school` AS select `tblschool`.`intSchoolID` AS `intSchoolID`,`tbllogin`.`intLoginID` AS `intLoginID`,`tblschool`.`strSchoolName` AS `strSchoolName`,`tblschool`.`strSchoolAddress` AS `strSchoolAddress`,`tblschool`.`strSchoolRegNo` AS `strSchoolRegNo`,`tbllogin`.`strEmail` AS `strEmail`,`tbllogin`.`strStatus` AS `strStatus` from (`tbllogin` join `tblschool` on((`tbllogin`.`intLoginID` = `tblschool`.`intLoginID`))) */;

/*View structure for view staff */

/*!50001 DROP TABLE IF EXISTS `staff` */;
/*!50001 DROP VIEW IF EXISTS `staff` */;

/*!50001 CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `staff` AS select `tblstaff`.`intStaffID` AS `intStaffID`,`tbllogin`.`intLoginID` AS `intLoginID`,`tblstaff`.`intSchoolID` AS `intSchoolID`,`tblstaff`.`strFirstName` AS `strFirstName`,`tblstaff`.`strLastName` AS `strLastName`,`tbllogin`.`strEmail` AS `strEmail`,`tblstaff`.`strMobileNo` AS `strMobileNo`,`tblstaff`.`strDesignation` AS `strDesignation`,`tbllogin`.`strStatus` AS `strStatus` from (`tbllogin` join `tblstaff` on((`tbllogin`.`intLoginID` = `tblstaff`.`intLoginID`))) */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
