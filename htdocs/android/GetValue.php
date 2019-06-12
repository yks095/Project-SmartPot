<?php include "./ServerName.php";

  $con = mysqli_connect($server_name, $user_name, $user_password, $db_name, $port);
  $query = "SELECT auto, limited, pumptime FROM smartpot where potName = 'test' ";
  $result = mysqli_query($con, $query);
  while ($row = mysqli_fetch_array($result)) {
    echo '?'. $row[0]. $row[1]. $row[2];
  }

  mysqli_close($con);
?>
