require('dotenv').config()
const express = require('express')
const port = process.env.PORT || 8081
const app = express ();

app.use(express.json())
app.use(express.urlencoded({ extended: true }))

const route = require('./routes/index.js')
route(app)

app.listen(port, () => {
    console.log("Server listening at http://localhost:" + port);
});