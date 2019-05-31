<?php
  $con = mysqli_connect("localhost", "root", "smartpot", "arduino", "3307");
  $potCode = $_POST["potCode"];
  $statement = mysqli_prepare($con, "SELECT * FROM flower WHERE potCode = ?");
  mysqli_stmt_bind_param($statement, "s", $potCode);
  mysqli_execute($statement);
  mysqli_stmt_store_result($statement);
  $response = array();
  $response["success"] = true;
  while(mysqli_stmt_fetch($statement)){
    $response["success"] = false;
    $response["potCode"] = $potCode;
  }
  echo json_encode($response);
 ?>
