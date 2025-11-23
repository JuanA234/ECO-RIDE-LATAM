-- Insert initial notification templates

-- Reservation Confirmed Template
INSERT INTO templates (code, channel, subject, body) VALUES
    ('reservation-confirmed', 'EMAIL',
     'Reservation Confirmed - Trip #{{tripId}}',
     '<html>
      <body>
        <h1>🎉 Reservation Confirmed!</h1>
        <p>Hi <strong>{{passengerName}}</strong>,</p>
        <p>Your reservation for trip <strong>#{{tripId}}</strong> has been confirmed.</p>
        <hr>
        <p><strong>Route:</strong> {{origin}} → {{destination}}</p>
        <p><strong>Departure:</strong> {{startTime}}</p>
        <hr>
        <p>See you soon!</p>
        <p><em>EcoRide Team</em></p>
      </body>
      </html>');

-- Reservation Cancelled Template
INSERT INTO templates (code, channel, subject, body) VALUES
    ('reservation-cancelled', 'EMAIL',
     'Reservation Cancelled - Trip #{{tripId}}',
     '<html>
      <body>
        <h1>❌ Reservation Cancelled</h1>
        <p>Hi <strong>{{passengerName}}</strong>,</p>
        <p>Your reservation for trip <strong>#{{tripId}}</strong> has been cancelled.</p>
        <p><strong>Reason:</strong> {{reason}}</p>
        <hr>
        <p>If you have any questions, please contact us.</p>
        <p><em>EcoRide Team</em></p>
      </body>
      </html>');

-- Payment Confirmed Template
INSERT INTO templates (code, channel, subject, body) VALUES
    ('payment-confirmed', 'EMAIL',
     'Payment Confirmed - {{amount}} {{currency}}',
     '<html>
      <body>
        <h1>✅ Payment Successful</h1>
        <p>Your payment of <strong>{{amount}} {{currency}}</strong> has been processed successfully.</p>
        <p><strong>Transaction ID:</strong> {{chargeId}}</p>
        <hr>
        <p>Thank you for using EcoRide!</p>
        <p><em>EcoRide Team</em></p>
      </body>
      </html>');

-- Payment Failed Template
INSERT INTO templates (code, channel, subject, body) VALUES
    ('payment-failed', 'EMAIL',
     'Payment Failed - Action Required',
     '<html>
      <body>
        <h1>⚠️ Payment Failed</h1>
        <p>Hi <strong>{{passengerName}}</strong>,</p>
        <p>We were unable to process your payment for trip <strong>#{{tripId}}</strong>.</p>
        <p><strong>Reason:</strong> {{reason}}</p>
        <hr>
        <p>Please update your payment method and try again.</p>
        <p><em>EcoRide Team</em></p>
      </body>
      </html>');

-- Trip Reminder Template
INSERT INTO templates (code, channel, subject, body) VALUES
    ('trip-reminder', 'EMAIL',
     'Trip Reminder - Tomorrow at {{startTime}}',
     '<html>
      <body>
        <h1>⏰ Trip Reminder</h1>
        <p>Hi <strong>{{passengerName}}</strong>,</p>
        <p>This is a reminder that your trip is <strong>tomorrow</strong>!</p>
        <hr>
        <p><strong>Route:</strong> {{origin}} → {{destination}}</p>
        <p><strong>Departure:</strong> {{startTime}}</p>
        <p><strong>Driver:</strong> {{driverName}}</p>
        <p><strong>Vehicle:</strong> {{carPlate}}</p>
        <hr>
        <p>Have a great trip!</p>
        <p><em>EcoRide Team</em></p>
      </body>
      </html>');

-- SMS Template for Reservation Confirmed
INSERT INTO templates (code, channel, subject, body) VALUES
    ('reservation-confirmed-sms', 'SMS',
     NULL,
     'EcoRide: Your reservation for trip #{{tripId}} is confirmed! Route: {{origin}} → {{destination}} at {{startTime}}');