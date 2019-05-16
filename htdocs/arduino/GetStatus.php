<?php
  $con = mysqli_connect("localhost", "root", "smartpot", "arduino", "3307");
  $potCode = $_POST["id"];

  $query = "SELECT auto FROM smartpot where potCode = '$potCode' ";
  $result = mysqli_query($con, $query);
  while ($row = mysqli_fetch_array($result)) {
    echo '?'. $row[0];
  }
  mysqli_close($con);
?>

<!-- <?php
  $con = mysqli_connect("localhost", "root", "smartpot", "arduino", "3307");

  $potCode = $_POST["id"];

  $query = "SELECT auto FROM smartpot where potCode = ? ";

  $statment = mysqli_prepare($con, $query);
  mysqli_stmt_bind_param($statment, "s", $potCode);
  mysqli_stmt_execute($statment);
  $response = array();
  $response["success"] = true;

  echo json_encode($response);

?> -->
