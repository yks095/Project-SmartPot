<?php
  $con = mysqli_connect("localhost", "root", "smartpot", "arduino", "3307");

  $potCode = $_POST["id"];
  $query = "SELECT manual FROM smartpot where potCode = '$potCode' ";
  $result = mysqli_query($con, $query);
  $row = mysqli_fetch_array($result);

  if($row[0] == '1'){
    $query = "SELECT manualpumptime FROM smartpot where potCode = '$potCode' ";
    $result2 = mysqli_query($con, $query);
  }

  while ($row2 = mysqli_fetch_array($result2)) {
    echo '?'. $row2[0];
  }

  mysqli_close($con);
?>
