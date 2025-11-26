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

-- Trip Completed (Driver)
INSERT INTO templates (code, channel, subject, body) VALUES
('trip-completed-driver', 'EMAIL',
 'Trip Completed - Trip #{{tripId}}',
 '<html><body>
    <h1>🎉 Trip Completed!</h1>
    <p>Hi <strong>{{driverName}}</strong>,</p>
    <p>Your trip <strong>#{{tripId}}</strong> has been completed successfully.</p>
    <p><strong>Completed at:</strong> {{completedAt}}</p>
    <hr>
    <p>Thank you for being part of EcoRide!</p>
    <p><em>EcoRide Team</em></p>
 </body></html>');

-- Trip Completed (Passenger)
INSERT INTO templates (code, channel, subject, body) VALUES
    ('trip-completed-passenger', 'EMAIL',
     'Trip Completed - Trip #{{tripId}}',
     '<html><body>
        <h1>✅ Trip Completed!</h1>
        <p>Hi <strong>{{passengerName}}</strong>,</p>
        <p>Your trip <strong>#{{tripId}}</strong> with driver <strong>{{driverName}}</strong> has been completed.</p>
        <hr>
        <p>We hope you had a great experience!</p>
        <p>Do not forget to rate your driver.</p>
        <p><em>EcoRide Team</em></p>
     </body></html>');

-- SMS Template for Reservation Confirmed
INSERT INTO templates (code, channel, subject, body) VALUES
    ('reservation-confirmed-sms', 'SMS',
     NULL,
     'EcoRide: Your reservation for trip #{{tripId}} is confirmed! Route: {{origin}} → {{destination}} at {{startTime}}');