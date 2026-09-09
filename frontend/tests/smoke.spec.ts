import { test, expect } from '@playwright/test';

const baseURL = process.env.BASE_URL || 'http://localhost:3001';

test.describe('NexusFlow smoke', () => {
  test('can open login and navigate to dashboard without crashing', async ({ page }) => {
    await page.goto(`${baseURL}/login`, { waitUntil: 'domcontentloaded' });
    await expect(page.locator('text=NexusFlow').first()).toBeVisible({ timeout: 15000 });

    // Perform login with demo credentials
    await page.fill('#login-email', 'admin@nexusflow.com');
    await page.fill('#login-password', 'admin123');
    await page.click('button[type="submit"]');

    // Verify redirect to dashboard and control tower title
    await expect(page).toHaveURL(/.*dashboard/, { timeout: 15000 });
    await expect(page.locator('h1, h2').first()).toBeVisible({ timeout: 15000 });
  });

  test('can navigate between pages (Shipments, Suppliers, Inventory, Risk, Analytics)', async ({ page }) => {
    await page.goto(`${baseURL}/login`, { waitUntil: 'domcontentloaded' });
    await page.fill('#login-email', 'admin@nexusflow.com');
    await page.fill('#login-password', 'admin123');
    await page.click('button[type="submit"]');
    await expect(page).toHaveURL(/.*dashboard/, { timeout: 15000 });

    // Navigate to Shipments
    await page.goto(`${baseURL}/shipments`, { waitUntil: 'domcontentloaded' });
    await expect(page.locator('text=Shipment').first()).toBeVisible({ timeout: 10000 });

    // Navigate to Suppliers
    await page.goto(`${baseURL}/suppliers`, { waitUntil: 'domcontentloaded' });
    await expect(page.locator('text=Supplier').first()).toBeVisible({ timeout: 10000 });

    // Navigate to Inventory
    await page.goto(`${baseURL}/inventory`, { waitUntil: 'domcontentloaded' });
    await expect(page.locator('text=Inventory').first()).toBeVisible({ timeout: 10000 });

    // Navigate to Risk Center
    await page.goto(`${baseURL}/risk`, { waitUntil: 'domcontentloaded' });
    await expect(page.locator('text=Risk').first()).toBeVisible({ timeout: 10000 });

    // Navigate to Analytics
    await page.goto(`${baseURL}/analytics`, { waitUntil: 'domcontentloaded' });
    await expect(page.locator('text=Analytics').first()).toBeVisible({ timeout: 10000 });
  });
});

