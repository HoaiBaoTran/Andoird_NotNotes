require('dotenv').config()
const express = require('express')
const port = process.env.PORT || 3000
const app = express ();

app.use(express.json())
app.use(express.urlencoded({extended: true}))

const route = require('./routes/index.js')
route(app)

app.listen(port, () => {
    console.log("Server Listening on PORT:", port);
});