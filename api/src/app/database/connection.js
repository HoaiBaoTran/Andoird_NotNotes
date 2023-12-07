require('dotenv').config()
const mysql = require('mysql2')
const {DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE} = process.env
const connection = mysql.createConnection({
    host: DB_HOST,
    user: DB_USER,
    password: DB_PASSWORD,
    database: DB_DATABASE,
});

connection.connect(function(err) {
    if (err) {
      console.error('Error connecting: ' + err.stack);
      return;
    }
  
    console.log('Connected as ID ' + connection.threadId);
});

module.exports = connection