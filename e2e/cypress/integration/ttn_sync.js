describe("TTN Sync", () => {
  it("Syncs the end device which exists in the TTN application", () => {
    cy.visit("http://localhost/climate");

    // Login
    cy.contains('Login').click();
    cy.getInputByLabel('Nutzername').type('admin');
    cy.getInputByLabel('Passwort').type('password');
    cy.get('form').submit();
    cy.wait(500);

    // Navigate to Administration area
    cy.get('.nav-item > .btn').first().get('button').first().click();
    cy.get('img[alt="Administration"]').parent().click();

    // Perform TTN Sync
    cy.contains('TTN Sync').click();
    cy.contains('Bist du sicher, dass du eine Synchronisation mit dem The Things Network durchführen möchtest?');
    cy.wait(500);
    cy.contains('Synchronisieren').click();
    cy.wait(500);

    // Check the imported device exists
    cy.contains('eui-70b3d57ed004fb7f');

    // Check if the device address is correct
    cy.contains('eui-70b3d57ed004fb7f').parent().parent().contains('Schlüssel').click();
    cy.getInputByLabel('Device Address').should('have.value', '260BAA60')
    cy.get('button.close').click();
  });
});
