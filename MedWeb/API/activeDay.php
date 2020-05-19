<?php
include_once 'connection.php';

$userID = $_POST['userID'];

//Get current date and add % to end
$getDate = date('Y-m-d');
$percentage= "%";
$currentDate = $getDate . '%';

$return_arr = array();

//Sql query
$query = $conn->prepare("select * from medicationData where dateTime LIKE :currentDate and userID=:userID ORDER BY dateTime DESC");

$success = $query->execute([
			'currentDate' => $currentDate,
			'userID' => $userID
		]);

$count = $query->rowCount();
		//Return medication data retrived in JSON format
		if ($count > 0) {
			
			while ($row = $query->fetch()) {
		
				$medicationID = $row['medicationID'];
		$dateTime = $row['dateTime'];
		$pillOneAmount = $row['pillOneAmount'];
		$pillTwoAmount = $row['pillTwoAmount'];
		$pillThreeAmount = $row['pillThreeAmount'];
		$status = $row['status'];

		$return_arr[] = array("medicationID" => $medicationID,
						"dateTime" => $dateTime,
						"pillOneAmount" => $pillOneAmount,
						"pillTwoAmount" => $pillTwoAmount,
						"pillThreeAmount" => $pillThreeAmount,
						"status" => $status);
			}
		}else {
			$return_arr[] = array("error" => "No medication data to display");	
		}
		
		echo json_encode($return_arr);
?>