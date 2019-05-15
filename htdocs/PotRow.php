<?php
$con = mysqli_connect("localhost", "root", "smartpot", "arduino", "3307");
  $userID = $_GET["userID"];
  if (mysqli_connect_errno())   {
    echo "Failed to connect to MySQL: " . mysqli_connect_error();
  }
  $result = mysqli_query($con, "SELECT potCode FROM SMARTPOT WHERE userID = '$userID' ");
  $num = mysqli_num_rows($result);

  
  // $row = mysqli_fetch_array($result);
  // $data = $row[0];
  // $response = array();
  // $response["success"] = true;
  echo $num;
  mysqli_close($con);
?>
