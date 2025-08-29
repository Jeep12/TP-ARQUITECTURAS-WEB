package com.terra.team.prural.email.template;

public class EmailTemplate {
    
    public static String getVerificationEmailTemplate(String verificationUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Verifica tu cuenta</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .button { display: inline-block; padding: 12px 24px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px; }
                    .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>¡Bienvenido a Prural!</h1>
                    </div>
                    <div class="content">
                        <h2>Verifica tu cuenta</h2>
                        <p>Gracias por registrarte. Para completar tu registro, por favor verifica tu dirección de email haciendo clic en el botón de abajo:</p>
                        <p style="text-align: center;">
                            <a href="%s" class="button">Verificar Email</a>
                        </p>
                        <p>Si el botón no funciona, puedes copiar y pegar este enlace en tu navegador:</p>
                        <p style="word-break: break-all; color: #666;">%s</p>
                        <p>Este enlace expirará en 24 horas por razones de seguridad.</p>
                    </div>
                    <div class="footer">
                        <p>Si no solicitaste esta verificación, puedes ignorar este email.</p>
                        <p>&copy; 2024 Terra API. Todos los derechos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(verificationUrl, verificationUrl);
    }
    
    public static String getPasswordResetEmailTemplate(String resetUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Restablecer contraseña</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #2196F3; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .button { display: inline-block; padding: 12px 24px; background-color: #2196F3; color: white; text-decoration: none; border-radius: 5px; }
                    .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Restablecer contraseña</h1>
                    </div>
                    <div class="content">
                        <h2>Has solicitado restablecer tu contraseña</h2>
                        <p>Para restablecer tu contraseña, haz clic en el botón de abajo:</p>
                        <p style="text-align: center;">
                            <a href="%s" class="button">Restablecer contraseña</a>
                        </p>
                        <p>Si el botón no funciona, puedes copiar y pegar este enlace en tu navegador:</p>
                        <p style="word-break: break-all; color: #666;">%s</p>
                        <p>Este enlace expirará en 1 hora por razones de seguridad.</p>
                        <p>Si no solicitaste restablecer tu contraseña, puedes ignorar este email.</p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2024 Terra API. Todos los derechos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(resetUrl, resetUrl);
    }
}
