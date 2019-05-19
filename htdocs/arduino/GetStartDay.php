<?php
  $con = mysqli_connect("localhost", "root", "smartpot", "arduino", "3307");

  $potCode = $_POST["id"];
  $query = "SELECT update_time FROM smartpot where potCode = '$potCode' ";
  $result = mysqli_query($con, $query);
  while ($row = mysqli_fetch_array($result)) {
    echo '?'. $row[0];
  }
  mysqli_close($con);
?>
