const express = require('express')
const router = express.Router()
const userController = require('../app/controllers/UserController')

router.post('/', userController.addUser)
router.get('/', userController.getAllUsers)
router.get('/:id', userController.getUserById)

module.exports = router