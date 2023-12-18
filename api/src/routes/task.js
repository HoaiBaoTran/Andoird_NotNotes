const express = require('express')
const taskController = require('../app/controllers/TaskController')
const router = express.Router()

router.get('/', taskController.getAllTasks)

module.exports = router
