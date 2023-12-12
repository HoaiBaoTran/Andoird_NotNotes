const userRouter = require('./user.js')
const taskRouter = require('./task.js')

function route(app) {
    app.use('/api/users', userRouter)
    app.use('/api/task',taskRouter)
}

module.exports = route