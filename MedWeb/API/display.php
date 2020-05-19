<?php
session_start();
include_once 'connection.php';
header('Content-Type: text/plain');

//Assign Variables
$rows=$_POST['numberRows'];
$order=$_POST['orderBy'];
$status=$_POST['medStatus'];
$request=$_POST['requestFunction'];


//Determine number of rows to be retrieved and create SQL
if($rows == 'All'){
	$sqlLimit="";
}else if($rows == '10' || $rows == '30' || $rows == '50'){
	$sqlLimit = " LIMIT ".$rows."";
}

//Determine order and create SQL
if($order == 'latest'){
	$sqlOrder="ORDER BY dateTime DESC ";
}else if($order == 'oldest'){
	$sqlOrder = "ORDER BY dateTime ASC ";
}

//Determine the status filter and create SQL
if ($status == 'Pending' || $status == 'Taken' || $status == 'Missed'){
	$sqlWhereJoin = " status='".$status."' ";
}else if($status == 'All'){
	$sqlWhereJoin ="";
}

//Determine the authorisation of the user and pass to appropriate function
if($request == 'Patient'){
	userRequest($sqlOrder, $sqlLimit, $sqlWhereJoin);
}else if($request == 'Doctor'){
	doctorRequest($sqlOrder, $sqlLimit, $sqlWhereJoin);
}


function userRequest($sqlOrder, $sqlLimit, $sqlWhereJoin){
	//Get unique user ID
			if(isset($_POST['app'])){
			$userID = $_POST['userID'];
		}else{
			$userID = ($_SESSION['userID']);
		}

	//If user filters for Pending, Taken or Missed then add AND to create complete SQL
	if (!empty($sqlWhereJoin)){
	$sqlWhere = "AND ".$sqlWhereJoin."";
	}
	
	//Create complete SQL query for user
    $sql = "SELECT * FROM medicationData WHERE userID='".$userID."' ".$sqlWhere." ".$sqlOrder."".$sqlLimit."";

	queryDatabase($sql);
}



function doctorRequest($sqlOrder, $sqlLimit, $sqlWhereJoin){

	//If user filters for Pending, Taken or Missed then add WHERE to create complete SQL
	if (!empty($sqlWhereJoin)){
	$sqlWhere = "WHERE ".$sqlWhereJoin."";
	}
	
	//Create complete SQL query for doctor
    $sql = "SELECT * FROM medicationData ".$sqlWhere."".$sqlOrder."".$sqlLimit."";

	queryDatabase($sql);
}


function queryDatabase($sqlQuery){
	global $conn;
	
	$return_arr = array();
	$sql = $sqlQuery;
	
	$result = $conn->query($sql);

$count = $result->rowCount();

		if ($count > 0) {
			
			while ($row = $result->fetch()) {
		
		$medicationID = $row['medicationID'];
		$userID = $row['userID'];
		$dateTime = $row['dateTime'];
		$pillOneAmount = $row['pillOneAmount'];
		$pillTwoAmount = $row['pillTwoAmount'];
		$pillThreeAmount = $row['pillThreeAmount'];
		$status = $row['status'];

		$return_arr[] = array("medicationID" => $medicationID,
						"userID" => $userID,
						"dateTime" => $dateTime,
						"pillOneAmount" => $pillOneAmount,
						"pillTwoAmount" => $pillTwoAmount,
						"pillThreeAmount" => $pillThreeAmount,
						"status" => $status);
		
			}
		
		}else {
			$return_arr[] = array("error" => "No intrusions to display");	
		}
		
		echo json_encode($return_arr);
	
}

?>