<?php
  $con = mysqli_connect("localhost", "root", "smartpot", "arduino", "3307");

  $potCode = $_POST["potCode"];
  $userID = $_POST["userID"];
  $manualPumpTime = $_POST["manualPumpTime"];

  if (mysqli_connect_errno())   {
    echo "Failed to connect to MySQL: " . mysqli_connect_error();
  }

  mysqli_query($con,"UPDATE SMARTPOT SET manualPumpTime='$manualPumpTime' WHERE userID = '$userID' ");

  $response = array();
  $response["success"] = true;

  echo json_encode($response);
?>
