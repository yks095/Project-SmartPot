<?php
$con = mysqli_connect("localhost", "root", "smartpot", "arduino", "3307");
  $potCode = $_POST["potCode"];
  $potName = $_POST["potName"];
  $userID = $_POST["userID"];

  $statment = mysqli_prepare($con, "INSERT INTO SMARTPOT (potCode, potName, userID, startday) VALUES (?, ?, ?, now())");
  mysqli_stmt_bind_param($statment, "sss", $potCode, $potName, $userID);
  mysqli_stmt_execute($statment);

  $response = array();
  $response["success"] = true;

  echo json_encode($response);
?>
