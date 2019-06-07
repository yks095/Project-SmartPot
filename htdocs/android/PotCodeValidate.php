<?php
  $con = mysqli_connect("localhost", "root", "smartpot", "arduino", "3307");
  $potCode = $_POST["potCode"];
  $statement = mysqli_prepare($con, "SELECT potname FROM smartpot WHERE potCode = ? AND potname is NULL");
  mysqli_stmt_bind_param($statement, "s", $potCode);
  mysqli_execute($statement);
  mysqli_stmt_store_result($statement);
  $response = array();
  $response["success"] = false;

  while(mysqli_stmt_fetch($statement)){
    $response["success"] = true;
    $response["potCode"] = $potCode;
  }
  echo json_encode($response);
 ?>
