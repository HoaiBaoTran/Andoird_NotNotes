const db = require('../database/connection')

class UserController {
    index (req, res, next) {
        res.json({
            code: 0,
            message: 'Account route'
        })
    }

    getAllUsers(req, res, next) {
        db.connect(function(err) {
            if (err) throw err;
            //Select all customers and return the result object:
            db.query("SELECT * FROM users", function (err, result, fields) {
              if (err) throw err;
              res.json({
                code: 200,
                message: 'Get all users success',
                data: result
              });
            });
          });
    }
}

module.exports = new UserController