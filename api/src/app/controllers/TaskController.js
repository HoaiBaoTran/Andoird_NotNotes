const db = require('../database/connection')

class Task {
    getAllTasks (req, res, next) {
        db.connect(function(err) {
            if (err) throw err;
            //Select all customers and return the result object:
            db.query("SELECT * FROM task", (err, result, fields) => {
                if (err) throw err;
                res.json({
                    code: 200,
                    message: 'Get all tasks success',
                    data: result
                });
            });
        });
    }
}

module.exports = new Task()